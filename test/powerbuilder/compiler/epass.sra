$PBExportHeader$epass.sra
$PBExportComments$// confSection:bo/generic/epass
forward
global type epass from application
end type
global n_tr sqlca
global dynamicdescriptionarea sqlda
global dynamicstagingarea sqlsa
global error error
global message message
end forward

global variables
n_cst_appmanager     gnv_app
n_tr                 gtr_fd
string               gs_current_directory
constant string      gs_copyright = Char(169) + ' 1999-' + string (today (), "yyyy") + ' Bravura Solutions Limited'
boolean              gb_batch_mode = false
n_testing_framework  gn_testing
any null
powerobject invalid
end variables

global type epass from application
string appname = "epass"
string microhelpdefault = "Ready"
string displayname = "ePASS"
end type
global epass epass

on epass.create
appname="epass"
message=create message
sqlca=create n_tr
sqlda=create dynamicdescriptionarea
sqlsa=create dynamicstagingarea
error=create error
end on

on epass.destroy
destroy(sqlca)
destroy(sqlda)
destroy(sqlsa)
destroy(error)
destroy(message)
end on

event open;// JLL ECS 05/11/2001	P1559		ensure current directory is set properly

long ll_size, ll_return
string ls_lib_list

// set current directory
classdefinition lcc
long ll_pos

setpointer(hourglass!)

u_win32_api win32
u_win32_api = win32
setnull(null)

if handle(getapplication())=0 then
	lcc=sqlca.classdefinition
	gs_current_directory=lcc.libraryname
	gs_current_directory=Left(gs_current_directory, lastpos(gs_current_directory, '\') - 1)
else
	gs_current_directory=space(1024)
	u_win32_api.GetModuleFileName(0, gs_current_directory, 1024)
end if

ll_pos=1
do while Pos(gs_current_directory,'\',ll_pos)>0
	ll_pos=Pos(gs_current_directory,'\',ll_pos)+1
loop
gs_current_directory=Left(gs_current_directory,ll_pos -2)

if ChangeDirectory(gs_current_directory) = -1 then
	f_log (populateError (0, "failed to set current directory, how is this possible?"))
end if

if Right(gs_current_directory,1)<>'\' then gs_current_directory+='\'

if Handle(GetApplication())<>0 then
	FileDelete(gs_current_directory+'epassfund.pbd')
end if

// initialise the random number generator
Randomize(0)

gn_testing = create n_testing_framework

gnv_app = create n_cst_appmanager
gnv_app.event pfc_open(commandline)
end event

event systemerror;
if isvalid(gnv_app) then
	gnv_app.state.of_log_error(error.object + "." + error.ObjectEvent + ' at line '+String(Error.Line) + ": " + error.text)
end if

MessageBox("ePASS Error", 'Application terminated.~r~n~r~n'+error.text)
HALT
end event

