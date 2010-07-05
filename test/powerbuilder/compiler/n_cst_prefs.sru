$PBExportHeader$n_cst_prefs.sru
$PBExportComments$epassgd.pbl
forward
global type n_cst_prefs from nonvisualobject
end type
type str_preferences from structure within n_cst_prefs
end type
end forward

type str_preferences from structure
	string		s_value
end type

global type n_cst_prefs from nonvisualobject
event ue_documentation ( )
event ue_test ( ) throws exception
end type
global n_cst_prefs n_cst_prefs

type variables
private n_ds ids_preferences
end variables

forward prototypes
public function string of_getpref (integer ai_preference_id)
public subroutine of_setprefs ()
public function string of_getpref (boolean ab_refresh, integer ai_preference_id)
public function integer of_setpref (integer ai_pref, string as_value)
public function string of_getpref (string as_entity_type, string as_entity_id, long al_preference_id, boolean ab_refresh)
public function integer of_setpref (string as_entity_type, string as_entity_id, long al_preference_id, string as_value)
public function string of_getpref (string as_entity_type, string as_entity_id, long al_preference_id)
end prototypes

event ue_documentation;//	JLL ECS	11 Jun 2002		P2815		AP Multiple Productisation, re-wrote caching model to use a datastore
//												return preference values for a specific entity

end event

event ue_test();string ls_original, ls_current

ls_original=f_getpref(1)

f_setpref(1,'XXXXX')

f_assert(f_getpref(1)='XXXXX', 'Temporary preference value was not set.')

select value
into :ls_current
from preferences
where preference_id=1
using gtr_fd;

f_assert(ls_original=ls_current, 'Preference value was set in the database.')

f_setpref(1,'Y')
f_assert(f_is_pref_on(1),'Testing boolean preference with "Y"')

f_setpref(1,'yes')
f_assert(f_is_pref_on(1),'Testing boolean preference with "yes"')

f_setpref(1,'N')
f_assert(not f_is_pref_on(1),'Testing boolean preference with "N"')

f_setpref(1,'no')
f_assert(not f_is_pref_on(1),'Testing boolean preference with "no"')

f_resetprefs()

f_assert(f_getpref(1)=ls_original, 'Preference value was not restored.')

end event

public function string of_getpref (integer ai_preference_id);//****************************************************************
//
//		Function Technical Documentation
//
//	Name:				of_getpref
//
//	Description:	return the value of a preference
//
//	Parameters:		integer	ai_preference_id
//
//	Return Value:	string
//
//****************************************************************

return of_getpref('USER',gnv_app.of_getuserid(),ai_preference_id,false)

end function

public subroutine of_setprefs ();//****************************************************************
//
//		Function Technical Documentation
//
//	Name:				of_setprefs
//
//	Description:	Clear the preference cache, only called in select fund process.
//
//****************************************************************

ids_preferences.reset()
ids_preferences.retrieve('','',0)

end subroutine

public function string of_getpref (boolean ab_refresh, integer ai_preference_id);//****************************************************************
//
//		Function Technical Documentation
//
//	Name:				of_getpref
//
//	Description:	return the value of a preference
//
//	Parameters:		boolean	ab_refresh
//						integer	ai_preference_id
//
//	Return Value:	string
//
//****************************************************************

return of_getpref('USER',gnv_app.of_getuserid(),ai_preference_id,ab_refresh)

end function

public function integer of_setpref (integer ai_pref, string as_value);//****************************************************************
//
//		Function Technical Documentation
//
//	Name:				of_setpref
//
//	Description:	set the value of a preference
//
//	Parameters:		integer	al_preference_id
//						string	as_value
//
//	Return Value:	string
//
//****************************************************************

return of_setpref('USER',gnv_app.of_getuserid(),ai_pref,as_value)

end function

public function string of_getpref (string as_entity_type, string as_entity_id, long al_preference_id, boolean ab_refresh);//****************************************************************
//
//		Function Technical Documentation
//
//	Name:				of_getpref
//
//	Description:	return the value of a preference, for a specific entity
//
//	Parameters:		string	as_entity_type
//						string	as_entity_id
//						long		al_preference_id
//						boolean	ab_refresh
//
//	Return Value:	string
//
//****************************************************************

long ll_row, ll_row2
string ls_find, ls_ret, ls_type, ls_null

// first find the default value from the preferences table
ls_find='preference_id='+string(al_preference_id)+' and isnull(entity_type)'

ll_row=ids_preferences.find(ls_find,1,ids_preferences.rowcount())

if ab_refresh then
	// discard and re-retrieve
	if ll_row>0 then
		ids_preferences.rowsdiscard(ll_row,ll_row,primary!)
	end if
	ids_preferences.retrieve('','',al_preference_id)
	ll_row=ids_preferences.find(ls_find,1,ids_preferences.rowcount())
end if

if ll_row < 1 then
	// not defined in this environment, so return ''
	if handle(getapplication())=0 then
		Messagebox('ePASS Warning', 'Preference '+string(al_preference_id)+' is not defined in this environment')
		ll_row2=ids_preferences.insertrow(0)
		setnull(ls_null)
		ids_preferences.object.entity_type[ll_row2]=ls_null
		ids_preferences.object.entity_key[ll_row2]=ls_null
		ids_preferences.object.preference_id[ll_row2]=al_preference_id
		ids_preferences.object.value[ll_row2]=''
	end if
	ls_ret=''
	return ls_ret
end if


// check that the population category matches the passed in entity type
ls_type=ids_preferences.object.population_category[ll_row]

if ls_type = as_entity_type then
	
	// have we already retrieved the values for this specific entity?
	ll_row2=ids_preferences.find('entity_type="'+as_entity_type+'" and entity_key="'+&
				f_escape(as_entity_id)+'"',1,ids_preferences.rowcount())
	
	if ll_row2 < 1 then
		// if not, then try to retrieve them
		ids_preferences.retrieve(as_entity_type,as_entity_id,0)
		ab_refresh=false
		ll_row2=ids_preferences.find('entity_type="'+as_entity_type+'" and entity_key="'+&
					f_escape(as_entity_id)+'"',1,ids_preferences.rowcount())
					
		if ll_row2 <1 then
			// still no record, so insert a dummy one to prevent a re-retrieve
			ll_row2=ids_preferences.insertrow(0)
			ids_preferences.object.entity_type[ll_row2]=as_entity_type
			ids_preferences.object.entity_key[ll_row2]=as_entity_id
			
		end if
	end if
	
	// find the preference row for this entity
	ls_find='preference_id='+string(al_preference_id)+' and entity_type="'+&
				as_entity_type+'" and entity_key="'+f_escape(as_entity_id)+'"'
	
	ll_row2=ids_preferences.find(ls_find,1,ids_preferences.rowcount())
	
	if ab_refresh then
		// discard and re-retrieve
		if ll_row2>0 then
			ids_preferences.rowsdiscard(ll_row2,ll_row2,primary!)
		end if
		ids_preferences.retrieve(as_entity_type,as_entity_id,al_preference_id)
		ll_row2=ids_preferences.find(ls_find,1,ids_preferences.rowcount())
	end if
	
	// if found then set ll_row to point to this record, otherwise return the default value
	if ll_row2 >0 then ll_row=ll_row2
else
	// developer only warning about preference misuse, you could put a breakpoint here to find the offending call
	if handle(getapplication())=0 and not (ls_type='' and as_entity_type='USER') then
		Messagebox('ePASS Warning','Preference '+string(al_preference_id)+' is flagged as entity type '+&
						ls_type+' but I was passed '+as_entity_type)
		ids_preferences.object.population_category[ll_row]=as_entity_type
	end if
end if

ls_ret=ids_preferences.object.value[ll_row]
if isnull(ls_ret) then ls_ret=''

return ls_ret
end function

public function integer of_setpref (string as_entity_type, string as_entity_id, long al_preference_id, string as_value);//****************************************************************
//
//		Function Technical Documentation
//
//	Name:				of_setpref
//
//	Description:	set the value of a preference for a specific entity
//
//	Parameters:		string	as_entity_type
//						string	as_entity_id
//						integer	al_preference_id
//						string	as_value
//
//	Return Value:	string
//
//****************************************************************

// PREFERENCES WITH population_category = NULL CANNOT BE CHANGED BY THIS METHOD ANY LONGER

// instead this method is used to change the contents of the datastore without changing the database
// so that unit testing can be achieved easily

integer li_ret
long ll_row
string ls_cat

if isnull(as_value) then as_value=''

ll_row=ids_preferences.find('preference_id='+string(al_preference_id)+' and isnull(entity_type)',&
		1,ids_preferences.rowcount())

if ll_row <1 then 
	ll_row=ids_preferences.insertrow(0)
	ids_preferences.object.preference_id[ll_row]=al_preference_id
end if

ls_cat=ids_preferences.object.population_category[ll_row]

if isnull(ls_cat) or ls_cat='' then
	ids_preferences.object.value[ll_row]=as_value
	return 1
end if

// update the entity specific value (if it exists)
update entity_preferences set value=:as_value
where preference_id=:al_preference_id
and entity_type=:as_entity_type
and entity_key=:as_entity_id
and exists (
	select 1 
	from preferences 
	where preference_id=entity_preferences.preference_id
	and population_category=entity_preferences.entity_type
)
using gtr_fd;

if gtr_fd.sqlcode=0 and gtr_fd.sqlnrows=0 then
	// ok, there wasn't an entity specific value (yet)
	
	// try to insert an entity specific value if the preference population_category matches the passed in entity type
	insert into entity_preferences (entity_type, entity_key, preference_id, value)
	select population_category, :as_entity_id, preference_id, :as_value
	from preferences
	where preference_id=:al_preference_id
	and population_category=:as_entity_type
	using gtr_fd;
end if

if gtr_fd.sqlcode<0 then
	// general db error
	Messagebox('ePASS Error','There was a database error while attempting to set preference '+string(al_preference_id)+&
			' to '+as_value+'.~r~nThe error was:~r~n'+gtr_fd.sqlerrtext)
	li_ret=-1
elseif gtr_fd.sqlnrows<>1 then
	// none of the above statements did anything so the preference is probably misconfigured/misused, display a warning.
	Messagebox('ePASS Error','There was a database error while attempting to set preference '+string(al_preference_id)+&
			' to '+as_value+'.~r~nThe value was not successfully updated')
	li_ret=-1
else
	li_ret=1
	// force the cache datastore to be refreshed
	of_getpref(as_entity_type,as_entity_id,al_preference_id,true)
end if

return li_ret
end function

public function string of_getpref (string as_entity_type, string as_entity_id, long al_preference_id);//****************************************************************
//
//		Function Technical Documentation
//
//	Name:				of_getpref
//
//	Description:	return the value of a preference
//
//	Parameters:		string	as_entity_type
//						string	as_entity_id
//						long		al_preference_id
//
//	Return Value:	string
//
//****************************************************************

return of_getpref(as_entity_type,as_entity_id,al_preference_id,false)
end function

on n_cst_prefs.create
call super::create
TriggerEvent( this, "constructor" )
end on

on n_cst_prefs.destroy
TriggerEvent( this, "destructor" )
call super::destroy
end on

event constructor;ids_preferences=create n_ds
ids_preferences.dataobject='d_preferences_cache'
ids_preferences.settransobject(gtr_fd)
ids_preferences.of_setappend(true)

this.of_setprefs()
end event

