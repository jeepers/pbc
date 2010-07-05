$PBExportHeader$w_adv_pref_maint.srw
forward
global type w_adv_pref_maint from w_base_maint
end type
type cb_add from u_cb within w_adv_pref_maint
end type
type dw_category from u_dw within w_adv_pref_maint
end type
end forward

global type w_adv_pref_maint from w_base_maint
integer x = 0
integer y = 0
integer width = 3282
integer height = 1528
string title = "Maintain Advanced Preferences"
string menuname = "m_sheet_global_services"
cb_add cb_add
dw_category dw_category
end type
global w_adv_pref_maint w_adv_pref_maint

on w_adv_pref_maint.create
int iCurrent
call super::create
if IsValid(this.MenuID) then destroy(this.MenuID)
if this.MenuName = "m_sheet_global_services" then this.MenuID = create m_sheet_global_services
this.cb_add=create cb_add
this.dw_category=create dw_category
iCurrent=UpperBound(this.Control)
this.Control[iCurrent+1]=this.cb_add
this.Control[iCurrent+2]=this.dw_category
end on

on w_adv_pref_maint.destroy
call super::destroy
if IsValid(MenuID) then destroy(MenuID)
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
this.inv_resize.of_register (dw_1, "ScaleToRight&Bottom")
// dw_category does not need to be moved.

end event

event open;call super::open;///////////////////////////////////////////////////////////////
//
// Name:
//    Open ()
//
// Description:
//    The Open event of the Maintaint Advanced Preferences window
//
////////////////////////////////////////////////////////////////
//   Modification History:
// LAS, ECS, 05-Jan-2004, 7541 - Created
////////////////////////////////////////////////////////////////

int lx
int ly
int lw

if isvalid (w_navigator) then
	lx = w_navigator.x
	ly = w_navigator.y
	lw = w_navigator.width
	
	if (this.x < lx + lw) then
		this.move (lx + lw, ly)
	end if
end if

dw_1.of_retrieve ()
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
n_cst_gd_funcs  lnv_gd_funcs

dw_1.accepttext ()

dw_1.setredraw (false)
ls_original_filter = dw_1.object.datawindow.table.filter
if isnull (ls_original_filter) then ls_original_filter = ''
if ls_original_filter = '?' then ls_original_filter = ''

dw_1.setfilter ("")
dw_1.filter ()

// Added elements
ll_pos = 0
ll_loop = 0
do while ll_loop < dw_1.rowcount()
	ll_loop = dw_1.find ("isrownew () and isrowmodified ()", ll_loop + 1, dw_1.rowcount ())
	if ll_loop < 1 then exit
	
	ll_pos ++
	ls_added [ll_pos] = dw_1.object.category_code [ll_loop] + " \ " + dw_1.object.preference_code [ll_loop]
loop

// Modified elements
ll_pos = 0
ll_loop = 0
do while ll_loop < dw_1.rowcount()
	ll_loop = dw_1.find ("isrowmodified () and not isrownew ()", ll_loop + 1, dw_1.rowcount ())
	if ll_loop < 1 then exit
	
	ll_pos ++
	ls_modifed [ll_pos] = dw_1.object.category_code [ll_loop] + " \ " + dw_1.object.preference_code [ll_loop]
loop

// Deleted elements
ll_pos = 0
for ll_loop = 1 to dw_1.deletedcount ()
	ll_pos ++
	ls_deleted [ll_pos] = dw_1.object.category_code.delete [ll_loop] + " \ " + dw_1.object.preference_code.delete [ll_loop]
next

dw_1.setfilter (ls_original_filter)
dw_1.filter ()
dw_1.setredraw (true)

li_rc = super::event pfc_save ()

if li_rc > 0 then
	// Write audit event [Advanced Preference Added]
	for ll_loop = lowerbound (ls_added) to upperbound (ls_added)
		lnv_gd_funcs.of_WriteAuditEvent (3883, "category_code\preference_code = " + ls_added [ll_loop])
	next
	
	// Write audit event [Advanced Preference Modified]
	for ll_loop = lowerbound (ls_modifed) to upperbound (ls_modifed)
		lnv_gd_funcs.of_WriteAuditEvent (3884, "category_code\preference_code = " + ls_modifed [ll_loop])
	next
	
	// Write audit event [Advanced Preference Deleted]
	for ll_loop = lowerbound (ls_deleted) to upperbound (ls_deleted)
		lnv_gd_funcs.of_WriteAuditEvent (3885, "category_code\preference_code = " + ls_deleted [ll_loop])
	next
end if

return li_rc
end event

type dw_1 from w_base_maint`dw_1 within w_adv_pref_maint
integer y = 152
integer width = 3177
integer height = 1036
integer taborder = 20
string dataobject = "d_adv_pref_maint"
boolean ib_rmbmenu = false
boolean ib_rmbfocuschange = false
end type

event dw_1::pfc_retrieve;call super::pfc_retrieve;///////////////////////////////////////////////////////////////
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

type cb_saveclose from w_base_maint`cb_saveclose within w_adv_pref_maint
integer x = 32
integer y = 1224
integer taborder = 30
end type

type cb_delete from w_base_maint`cb_delete within w_adv_pref_maint
integer x = 763
integer y = 1224
integer taborder = 50
end type

event cb_delete::clicked;///////////////////////////////////////////////////////////////
//
// Name:
//    cb_delete::clicked ()
//
// Description:
//    The clicked event of the Delete button
//    Deletes the currently selected row
//
////////////////////////////////////////////////////////////////
//   Modification History:
// LAS, ECS, 05-Jan-2004, 7541 - Created
////////////////////////////////////////////////////////////////

long ll_row

ll_row = dw_1.getrow ()

if ll_row > 0 then
	if messagebox('ePASS Question', 'Are you sure you wish to delete this item?', Question!, YesNo!, 1) = 1 then
		if dw_1.deleterow (ll_row) > 0 then
			dw_1.setfocus ()
			if ll_row > 1 then
				dw_1.scrolltorow (ll_row - 1)
			end if
		end if
	end if
end if

// Disable this button if no rows exist
if dw_1.rowcount () = 0 then
	cb_delete.enabled = false
end if
end event

type cb_cancel from w_base_maint`cb_cancel within w_adv_pref_maint
integer x = 1129
integer y = 1224
integer taborder = 60
end type

type cb_add from u_cb within w_adv_pref_maint
integer x = 398
integer y = 1224
integer taborder = 40
string text = "&Add"
end type

event clicked;call super::clicked;///////////////////////////////////////////////////////////////
//
// Name:
//    cb_add::clicked ()
//
// Description:
//    The clicked event of the Add button
//
////////////////////////////////////////////////////////////////
//   Modification History:
// LAS, ECS, 05-Jan-2004, 7541 - Created
////////////////////////////////////////////////////////////////

long ll_row

ll_row = dw_1.insertrow (0)

if ll_row > 0 then
	dw_1.setfocus ()
	dw_1.scrolltorow (ll_row)
	
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
boolean ib_rmbmenu = false
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

if row = 1 then
	if not isnull (data) and data > "" then
		dw_1.SetFilter ("category_code = '" + data + "'")
		dw_1.Filter ()
		
		if dw_1.rowcount () > 0 then
			dw_1.setrow (1)
			dw_1.selectrow (0, false)
			dw_1.selectrow (1, true)
			dw_1.scrolltorow (1)
			
			// Enable the delete button if rows exist
			cb_delete.enabled = true
		else
			// Disable the delete button if no rows exist
			cb_delete.enabled = false
		end if
		
		dw_1.object.category_code.initial = data
	end if
end if
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

ll_rc = this.retrieve ()

If ll_rc >= 0 then
	ll_rc = this.insertrow (0)
end if

if ll_rc > 0 then
	ll_rc = this.getchild ("category_code", ldwc)
	if ll_rc > 0 then
		if ldwc.rowcount () > 0 then
			ls_code = ldwc.getitemstring (1, "category_code")
			this.object.category_code [1] = ls_code
			this.event itemchanged (1, ldwo_null, ls_code)
		end if
		
		cb_delete.enabled = (ldwc.rowcount () > 0)
	end if
end if

return ll_rc
end event

event constructor;call super::constructor;this.of_settransobject (gtr_fd)

this.ib_rmbmenu = false
end event

