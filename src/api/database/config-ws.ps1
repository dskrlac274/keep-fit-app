DBName="keep_fit"
DBUser="postgres"
ScriptPath="./create.sql"

psql -U "$DBUser" -l

CheckDB=$(psql -U "$DBUser" -lqt | grep -c "$DBName")

if [ "$CheckDB" -eq 0 ]; then
    echo "Database '$DBName' does not exist."
    read -s -p "Enter password for user $DBUser: " password

    createdb -U "$DBUser" -h localhost -p 5432 -e "$DBName"
    echo "Database '$DBName' successfully created."

    psql -U "$DBUser" -d "$DBName" -f "$ScriptPath"
    echo "Tables created in database '$DBName'."
else
    echo "Database '$DBName' already exists."
    echo "Executing $ScriptPath..."
    psql -U "$DBUser" -d "$DBName" -f "$ScriptPath"
fi
