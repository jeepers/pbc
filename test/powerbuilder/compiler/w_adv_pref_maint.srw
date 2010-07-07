$PBExportHeader$w_adv_pref_maint.srw
forward
global type w_adv_pref_maint from w_sheet
end type
type dw_preferences from u_dw_adv_prefs within w_adv_pref_maint
end type
type cb_saveclose from u_cb within w_adv_pref_maint
end type
type cb_delete from u_cb within w_adv_pref_maint
end type
type cb_cancel from u_cb within w_adv_pref_maint
end type
type cb_add from u_cb within w_adv_pref_maint
end type
type dw_category from u_dw within w_adv_pref_maint
end type
end forward

global type w_adv_pref_maint from w_sheet
integer x = 0
integer y = 0
integer width = 3282
integer height = 1452
string title = "Maintain Advanced Preferences"
dw_preferences dw_preferences
cb_saveclose cb_saveclose
cb_delete cb_delete
cb_cancel cb_cancel
cb_add cb_add
dw_category dw_category
end type
global w_adv_pref_maint w_adv_pref_maint

type variables
long il_width
end variables

on w_adv_pref_maint.create
int iCurrent
call super::create
this.dw_preferences=create dw_preferences
this.cb_saveclose=create cb_saveclose
this.cb_delete=create cb_delete
this.cb_cancel=create cb_cancel
this.cb_add=create cb_add
this.dw_category=create dw_category
iCurrent=UpperBound(this.Control)
this.Control[iCurrent+1]=this.dw_preferences
this.Control[iCurrent+2]=this.cb_saveclose
this.Control[iCurrent+3]=this.cb_delete
this.Control[iCurrent+4]=this.cb_cancel
this.Control[iCurrent+5]=this.cb_add
this.Control[iCurrent+6]=this.dw_category
end on

on w_adv_pref_maint.destroy
call super::destroy
destroy(this.dw_preferences)
destroy(this.cb_saveclose)
destroy(this.cb_delete)
destroy(this.cb_cancel)
destroy(this.cb_add)
destroy(this.dw_category)
end on

event pfc_postopen;call super::pfc_postopen;///////////////////////////////////////////////////////////////
//
// Name:
//    pfc_postopen ()
//
// Description:
//    The post open event for the Maintain Advanced Preferences window
//
////////////////////////////////////////////////////////////////
//   Modification History:
// LAS, ECS, 05-Jan-2004, 7541 - Created
////////////////////////////////////////////////////////////////

// Ensure objects are positioned correctly
this.of_setresize (true)
this.inv_resize.of_register (cb_saveclose, "FixedToBottom")
this.inv_resize.of_register (cb_add, "FixedToBottom")
this.inv_resize.of_register (cb_delete, "FixedToBottom")
this.inv_resize.of_register (cb_cancel, "FixedToBottom")
this.inv_resize.of_register (dw_preferences, "ScaleToRight&Bottom")
// dw_category does not need to be moved.

end event

event open;call super::open;string ls_parm

ls_parm = message.stringparm
if ls_parm<>'' and not isnull(ls_parm) then
	dw_category.object.category_code.initial=ls_parm
end if
dw_preferences.of_retrieve ()
dw_category.of_retrieve ()

end event

event pfc_save;///////////////////////////////////////////////////////////////
//
// Name:
//    pfc_save ()
//
// Description:
//    write out changes to audit events upon success
//
// Returns: Integer
//    < 0 - Failed
//     0  - Nothing to save
//    > 0 - Success
//
////////////////////////////////////////////////////////////////
//   Modification History:
// LAS, ECS, 05-Jan-2004, 7541 - Created
////////////////////////////////////////////////////////////////

integer         li_rc
long            ll_loop
long            ll_pos
string          ls_added []
string          ls_modifed []
string          ls_deleted []
string          ls_original_filter

dw_preferences.accepttext ()

dw_preferences.setredraw (false)
ls_original_filter = dw_preferences.object.datawindow.table.filter
if isnull (ls_original_filter) then ls_original_filter = ''
if ls_original_filter = '?' then ls_original_filter = ''

dw_preferences.setfilter ("")
dw_preferences.filter ()

// Added elements
ll_pos = 0
ll_loop = 0
do while ll_loop < dw_preferences.rowcount()
	ll_loop = dw_preferences.find ("isrownew () and isrowmodified ()", ll_loop + 1, dw_preferences.rowcount ())
	if ll_loop < 1 then exit
	
	ll_pos ++
	ls_added [ll_pos] = dw_preferences.object.category_code [ll_loop] + " \ " + dw_preferences.object.preference_code [ll_loop]
loop

// Modified elements
ll_pos = 0
ll_loop = 0
do while ll_loop < dw_preferences.rowcount()
	ll_loop = dw_preferences.find ("isrowmodified () and not isrownew ()", ll_loop + 1, dw_preferences.rowcount ())
	if ll_loop < 1 then exit
	
	ll_pos ++
	ls_modifed [ll_pos] = dw_preferences.object.category_code [ll_loop] + " \ " + dw_preferences.object.preference_code [ll_loop]
loop

// Deleted elements
ll_pos = 0
for ll_loop = 1 to dw_preferences.deletedcount ()
	ll_pos ++
	ls_deleted [ll_pos] = dw_preferences.object.category_code.delete [ll_loop] + " \ " + dw_preferences.object.preference_code.delete [ll_loop]
next

dw_preferences.setfilter (ls_original_filter)
dw_preferences.filter ()
dw_preferences.setredraw (true)

li_rc = super::event pfc_save ()

if li_rc > 0 then
	// Write audit event [Advanced Preference Added]
	for ll_loop = lowerbound (ls_added) to upperbound (ls_added)
		f_write_audit_event(3883, "", "category_code\preference_code = " + ls_added [ll_loop], state)
	next
	
	// Write audit event [Advanced Preference Modified]
	for ll_loop = lowerbound (ls_modifed) to upperbound (ls_modifed)
		f_write_audit_event(3884, "", "category_code\preference_code = " + ls_modifed [ll_loop], state)
	next
	
	// Write audit event [Advanced Preference Deleted]
	for ll_loop = lowerbound (ls_deleted) to upperbound (ls_deleted)
		f_write_audit_event(3885, "", "category_code\preference_code = " + ls_deleted [ll_loop], state)
	next
end if

return li_rc
end event

type dw_preferences from u_dw_adv_prefs within w_adv_pref_maint
integer x = 32
integer y = 152
integer width = 3177
integer height = 1036
integer taborder = 20
boolean bringtotop = true
boolean ib_rmbfocuschange = false
end type

event constructor;call super::constructor;this.of_settransobject(gtr_fd)
il_width = long(dw_preferences.object.value.width)

end event

event dw_preferences::pfc_retrieve;call super::pfc_retrieve;///////////////////////////////////////////////////////////////
//
// Name:
//    pfc_retrieve ()
//
// Description:
//    Main data window retrieve
//
////////////////////////////////////////////////////////////////
//   Modification History:
// LAS, ECS, 05-Jan-2004, 7541 - Created
////////////////////////////////////////////////////////////////

long ll_rc

this.of_setrowselect (true)
this.of_setsort (true)
this.inv_sort.of_setcolumnheader (true)

ll_rc = this.retrieve ()

cb_delete.enabled = (ll_rc > 0)

return ll_rc
end event

type cb_saveclose from u_cb within w_adv_pref_maint
integer x = 32
integer y = 1224
integer taborder = 30
boolean bringtotop = true
string text = "Save/Close"
boolean default = true
end type

event clicked;call super::clicked;if	parent.trigger event pfc_save() >= 0 then
	close(parent)
end if
end event

type cb_delete from u_cb within w_adv_pref_maint
integer x = 763
integer y = 1224
integer taborder = 50
boolean bringtotop = true
string text = "&Delete"
end type

event cb_delete::clicked;
long ll_row

ll_row = dw_preferences.getrow ()

if ll_row > 0 then
	if messagebox('ePASS Question', 'Are you sure you wish to delete this item?', Question!, YesNo!, 1) = 1 then
		if dw_preferences.deleterow (ll_row) > 0 then
			dw_preferences.setfocus ()
			if ll_row > 1 then
				dw_preferences.scrolltorow (ll_row - 1)
			end if
		end if
	end if
end if

// Disable this button if no rows exist
if dw_preferences.rowcount () = 0 then
	cb_delete.enabled = false
end if
end event

type cb_cancel from u_cb within w_adv_pref_maint
integer x = 1129
integer y = 1224
integer taborder = 60
boolean bringtotop = true
string text = "&Cancel"
boolean cancel = true
end type

event clicked;call super::clicked;close(parent)
end event

type cb_add from u_cb within w_adv_pref_maint
integer x = 398
integer y = 1224
integer taborder = 40
string text = "&Add"
end type

event clicked;call super::clicked;
long ll_row

ll_row = dw_preferences.insertrow (0)

if ll_row > 0 then
	dw_preferences.setfocus ()
	dw_preferences.scrolltorow (ll_row)
	
	// Enable the Delete button
	if not cb_delete.enabled then
		cb_delete.enabled = true
	end if
end if
end event

type dw_category from u_dw within w_adv_pref_maint
integer x = 37
integer y = 28
integer width = 1609
integer height = 108
integer taborder = 10
boolean bringtotop = true
string dataobject = "d_dddw_adv_pref_cat"
boolean hscrollbar = false
boolean vscrollbar = false
boolean border = false
boolean livescroll = false
boolean ib_isupdateable = false
boolean ib_rmbfocuschange = false
end type

event itemchanged;call super::itemchanged;///////////////////////////////////////////////////////////////
//
// Name:
//    itemchanged ()
//
// Description:
//    Fired when the category is changed.
//
// Arguments:
//    row - Only 1 is valid
//    dwo - Not Used (Only one item)
//    data - The category selected
//
////////////////////////////////////////////////////////////////
//   Modification History:
// LAS, ECS, 05-Jan-2004, 7541 - Created
////////////////////////////////////////////////////////////////

string ls_modify
long ll_i, ll_width

try
	if row = 1 then
		if not isnull (data) and data > "" then
			dw_preferences.setredraw(false)
			dw_preferences.Filterex ("category_code = '" + data + "'")
			
			if dw_preferences.rowcount () > 0 then
				dw_preferences.setrow (1)
				dw_preferences.selectrow (0, false)
				dw_preferences.selectrow (1, true)
				dw_preferences.scrolltorow (1)
				
				// Enable the delete button if rows exist
				cb_delete.enabled = true
			else
				// Disable the delete button if no rows exist
				cb_delete.enabled = false
			end if
			
			dw_preferences.object.category_code.initial = data
			f_resize_dw_cols(dw_preferences)
			
			dw_preferences.object.value_bool.x = dw_preferences.object.value.x
			dw_preferences.object.value_bool.width = dw_preferences.object.value.width
			dw_preferences.of_init_category(data)
			
			ll_width = max(il_width, long(dw_preferences.object.value.width))
			
			ls_modify=&
				'value_t.width='+string(ll_width)+' '+&
				'value.width='+string(ll_width)+' '+&
				'value_bool.x='+dw_preferences.object.value.x+' '+&
				'value_bool.width='+string(ll_width)+' '
				
			for ll_i = lowerbound(dw_preferences.isa_dddws) to upperbound(dw_preferences.isa_dddws)
				ls_modify+=&
					dw_preferences.isa_dddws[ll_i]+'.x='+dw_preferences.object.value.x+' '+&
					dw_preferences.isa_dddws[ll_i]+'.width='+string(ll_width)+' '
			next
			
			dw_preferences.modifyex(ls_modify)
			
			dw_preferences.setredraw(true)
		end if
	end if
catch (throwable t)
	messagebox(t)
end try
end event

event pfc_retrieve;call super::pfc_retrieve;///////////////////////////////////////////////////////////////
//
// Name:
//    pfc_retrieve
//
// Description:
//    The retrive of the category drop down
//
////////////////////////////////////////////////////////////////
//   Modification History:
// LAS, ECS, 05-Jan-2004, 7541 - Created
////////////////////////////////////////////////////////////////

long ll_rc
string ls_code
datawindowchild ldwc
dwobject ldwo_null

setnull (ldwo_null)

try
	cb_delete.enabled = false
	
	retrieveex ()
	insertrow (0)
	
	if getchild ("category_code", ldwc)<>1 then return -1
	
	if ldwc.rowcount () > 0 then
		ls_code = this.object.category_code[1]
		if isnull(ls_code) then
			ls_code = ldwc.getitemstring (1, "category_code")
			this.object.category_code [1] = ls_code
		end if
		this.event itemchanged (1, ldwo_null, ls_code)
		cb_delete.enabled = true
	end if
	
	return ll_rc
catch (throwable t)
	return messagebox(t)
end try
end event

event constructor;call super::constructor;this.of_settransobject (gtr_fd)

end event

