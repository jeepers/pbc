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
end type
global n_cst_prefs n_cst_prefs

type variables
private:
n_ds ids_preferences
long il_last_pref=0

end variables

forward prototypes
public function string of_getpref (integer ai_preference_id)
public subroutine of_setprefs () throws exception
public function string of_getpref (boolean ab_refresh, integer ai_preference_id)
public function integer of_setpref (integer ai_pref, readonly string as_value)
public function string of_getpref (readonly string as_entity_type, readonly string as_entity_id, long al_preference_id, boolean ab_refresh)
public function string of_getpref_raw (readonly string as_entity_type, readonly string as_entity_id, long al_preference_id, boolean ab_refresh)
public function integer of_setpref (readonly string as_entity_type, readonly string as_entity_id, long al_preference_id, string as_value)
public function string of_getpref (readonly string as_entity_type, readonly string as_entity_id, long al_preference_id)
end prototypes

public function string of_getpref (integer ai_preference_id);
return f_explode(of_getpref_raw('USER',gnv_app.of_getuserid(),ai_preference_id,false))
end function

public subroutine of_setprefs () throws exception;//****************************************************************
//
//		Function Technical Documentation
//
//	Name:				of_setprefs
//
//	Description:	Clear the preference cache, only called in select fund process.
//
//****************************************************************

long ll_row

ids_preferences.reset()
ids_preferences.settransobject(gtr_fd)
ids_preferences.retrieve('','',0)

ll_row=0
do while ids_preferences.of_find_next('preference_id > getrow()',ll_row)
	ids_preferences.insertrow(ll_row)
	ids_preferences.object.preference_id[ll_row]=ll_row
loop

il_last_pref=ids_preferences.rowcount()
end subroutine

public function string of_getpref (boolean ab_refresh, integer ai_preference_id);
return f_explode(of_getpref_raw('USER',gnv_app.of_getuserid(),ai_preference_id,ab_refresh))
end function

public function integer of_setpref (integer ai_pref, readonly string as_value);
return of_setpref('USER',gnv_app.of_getuserid(),ai_pref,as_value)
end function

public function string of_getpref (readonly string as_entity_type, readonly string as_entity_id, long al_preference_id, boolean ab_refresh);
return f_explode(of_getpref_raw(as_entity_type, as_entity_id, al_preference_id, ab_refresh))
end function

public function string of_getpref_raw (readonly string as_entity_type, readonly string as_entity_id, long al_preference_id, boolean ab_refresh);//****************************************************************
//
//		Function Technical Documentation
//
//	Name:				of_getpref_raw
//
//	Description:	return the value of a preference, for a specific entity
//
//	Parameters:		string		as_entity_type		optional
//						string		as_entity_id			optional
//						long		al_preference_id
//						boolean	ab_refresh			optional
//
//	Return Value:	string
//
//****************************************************************

long ll_row, ll_row2
string ls_find, ls_ret, ls_type, ls_null
n_ds ln_ds

ln_ds=ids_preferences

// first find the default value from the preferences table
ll_row=al_preference_id
if ll_row < 1 or ll_row > il_last_pref then
	gnv_app.state.of_log_warning('Preference '+string(al_preference_id)+' is not defined in this environment')
	return ''
end if

if ab_refresh then
	// re-retrieve
	ln_ds.reselectrow(ll_row)
end if

// check that the population category matches the passed in entity type
ls_type=ln_ds.getItemString (ll_row, "population_category")

if ls_type = as_entity_type then
	
	// have we already retrieved the values for this specific entity?
	ll_row2=ln_ds.find('entity_type="'+as_entity_type+'" and entity_key="'+&
				f_escape(as_entity_id)+'"',il_last_pref+1,ln_ds.rowcount())
	
	if ll_row2 < 1 then
		// if not, then try to retrieve them
		ln_ds.retrieve(as_entity_type,as_entity_id,0)
		ab_refresh=false
		ll_row2=ln_ds.find('entity_type="'+as_entity_type+'" and entity_key="'+&
					f_escape(as_entity_id)+'"',il_last_pref+1,ln_ds.rowcount())
					
		if ll_row2 <1 then
			// still no record, so insert a dummy one to prevent a re-retrieve
			ll_row2=ln_ds.insertrow(0)
			ln_ds.object.entity_type[ll_row2]=as_entity_type
			ln_ds.object.entity_key[ll_row2]=as_entity_id
			
		end if
	end if
	
	// find the preference row for this entity
	ls_find='preference_id='+string(al_preference_id)+' and entity_type="'+&
				as_entity_type+'" and entity_key="'+f_escape(as_entity_id)+'"'
	
	ll_row2=ln_ds.find(ls_find,il_last_pref+1,ln_ds.rowcount())
	
	if ab_refresh then
		// discard and re-retrieve
		if ll_row2>0 then
			ln_ds.rowsdiscard(ll_row2,ll_row2,primary!)
		end if
		ln_ds.retrieve(as_entity_type,as_entity_id,al_preference_id)
		ll_row2=ln_ds.find(ls_find,il_last_pref+1,ln_ds.rowcount())
	end if
	
	// if found then set ll_row to point to this record, otherwise return the default value
	if ll_row2 >0 then ll_row=ll_row2

end if

ls_ret = ln_ds.getItemString (ll_row, "value")
if isnull(ls_ret) then ls_ret=''

return ls_ret
end function

public function integer of_setpref (readonly string as_entity_type, readonly string as_entity_id, long al_preference_id, string as_value);//****************************************************************
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

public function string of_getpref (readonly string as_entity_type, readonly string as_entity_id, long al_preference_id);
return f_explode(of_getpref_raw(as_entity_type,as_entity_id,al_preference_id,false))
end function

on n_cst_prefs.create
call super::create
TriggerEvent( this, "constructor" )
end on

on n_cst_prefs.destroy
TriggerEvent( this, "destructor" )
call super::destroy
end on

event constructor;n_ds ln_ds

ln_ds=create n_ds
ln_ds.dataobject='d_preferences_cache'
ln_ds.of_setappend(true)

ids_preferences = ln_ds
end event

