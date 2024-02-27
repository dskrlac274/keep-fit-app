$DBName = "keep_fit"
$DBUser = "postgres"
$ScriptPath = ".\delete.sql"

psql -U $DBUser -d $DBName -f $ScriptPath