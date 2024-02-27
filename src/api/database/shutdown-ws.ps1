$DBName = "keep_fit"
$DBUser = "postgres"

psql -U $DBUser -d postgres -c "SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE pg_stat_activity.datname = '$DBName' AND pid <> pg_backend_pid();"
psql -U $DBUser -d postgres -c "DROP DATABASE IF EXISTS $DBName;"