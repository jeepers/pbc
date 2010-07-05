$PBExportHeader$epass.sra
$PBExportComments$epass.pbl - SIR 884
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
n_cst_appmanager	gnv_app
n_tr		gtr_fd, gtr_gd, gtr_ed, gtr_md, gtr_ap
string		gs_current_directory
boolean		gb_debug=false
constant string gs_copyright = 'Copyright ' + char(169) + ' Essential Computer Systems 1999 - ' + string (today (), "yyyy")
string		gs_version = 'Version 2.4.0.17'
boolean		gb_batch_mode=false
n_testing_framework gn_testing
end variables

global type epass from application
string appname = "epass"
string microhelpdefault = "Ready"
string displayname = "ePASS"
end type
global epass epass

type prototypes
Function long GetCurrentDirectory(Long nBufferLength , ref string lpBuffer) Library "kernel32.dll" alias for "GetCurrentDirectoryA"
Function long SetCurrentDirectory( ref string lpBuffer) Library "kernel32.dll" alias for "SetCurrentDirectoryA"
Function long GetModuleFileNameA (long handle, ref string filename, long size) library "kernel32.dll"

end prototypes

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

if handle(getapplication())=0 then
	lcc=sqlca.classdefinition
	gs_current_directory=lcc.libraryname
	gs_current_directory=left(gs_current_directory, lastpos(gs_current_directory, '\') - 1)
else
	gs_current_directory=space(1024)
	GetModuleFileNameA(0,gs_current_directory,1024)
end if

ll_pos=1
do while pos(gs_current_directory,'\',ll_pos)>0
	ll_pos=pos(gs_current_directory,'\',ll_pos)+1
loop
gs_current_directory=left(gs_current_directory,ll_pos -2)

if SetCurrentDirectory(gs_current_directory) =0 then
	// failure... how?
end if

if right(gs_current_directory,1)<>'\' then gs_current_directory+='\'

if handle(getapplication())<>0 then
	filedelete('epassfund.pbd')
end if

// f_getversion is generated in the build process
gs_version=f_getversion()

// initialise the random number generator
randomize(0)

gn_testing=create n_testing_framework

gnv_app = create n_cst_appmanager
gnv_app.trigger event pfc_open(commandline)


end event

event systemerror;
string ls_msg

ls_msg = error.object + "::" + error.ObjectEvent + String(Error.Line) + ": " + error.text

f_log (ls_msg)
messageBox ("ePASS Error", ls_msg)
end event

