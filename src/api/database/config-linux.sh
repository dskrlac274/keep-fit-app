
DBName="keep_fit"
DBUser="postgres"
ScriptPath="./create.sql"

CheckDB=$(psql -U "$DBUser" -lqt | cut -d \| -f 1 | grep -wq "$DBName"; echo $?)

if [ "$CheckDB" -eq 1 ]; then
    createdb -U "$DBUser" -h localhost -p 5432 -e "$DBName"
    echo "Database '$DBName' successfully created."
else
    echo "Database '$DBName' already exists."
fi

echo "Executing $ScriptPath..."
psql -U "$DBUser" -d "$DBName" -f "$ScriptPath" -W
