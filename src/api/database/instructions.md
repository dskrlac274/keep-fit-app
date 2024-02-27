### Setting up PostgreSQL Database

#### 1. Install PostgreSQL:

If PostgreSQL isn't installed, install it (:

- **Download page:**
    ```bash
    https://www.postgresql.org/download/
    ```

#### 2. Add PostgreSQL to Environment Variables:

Update `PATH` to access PostgreSQL commands globally:

- **Linux-mint:**
    ```bash
    echo 'export PATH=$PATH:/usr/lib/postgresql/<version>/bin' >> ~/.bashrc
    ```

- **Windows:**
    ```
    1. Edit the system environment variables
    2. Environment Varaibles
    3. Path
    4. Add path C:\Program Files\PostgreSQL\<version>\bin
    ```
    Replace `<version>` with the installed PostgreSQL version.

#### 3. Initialize a Git Repository:

Clone Git repository from Github:

```bash
mkdir keeep-fit
cd keep-fit
git clone https://github.com/dskrlac274/keep-fit.git
```

#### 4. Open one of the config files:
1. Depending on your OS
    1.  **Linux:**
        ```bash
        Open config-linux.sh
        ```
 
        - Ensure you have added following command:
            ```bash
            sudo nano /etc/postgresql/14/main/pg_hba.conf 
            ```
        - Add this to the begining of the file
            ```bash
            local   all            postgres,<your-user>                        md5

            ```


    2. **Windows:**
        ```
        Open config-ws.ps1
        ```
2. Change database properties
    ```
    DB_NAME="ime_baze"
    DB_USER="korisnicko_ime"
    DB_PASS="lozinka"
    ```
3. Run specific script
    1.  **Linux:**
        ```bash
        ./config-linux.sh
        ```

    2. **Windows:**
        ```
        .\config-ws.ps1
        ```
4. Database should be successfully configured

### Changing Node.js server database usage
1. Position to database folder and open Database.js file:
    ```bash
    cd .\keep-fit\software\api\database\
    ```
2. Change follwoing code to match your databse configuration:
    ```bash
    this.pool = new Pool({
        user: 'postgres',
        host: 'localhost',
        database: 'keep_fit',
        password: '<your-password>',
        port: 5432
    })
    ```

### Running Node.js server  

1. Position to api folder:

    ```bash
    cd .\keep-fit\software\api\
    ```
2. Run next command:  

    ```bash
    npm i
    ```
    Given command install's all application external dependancies and it's mandatory for the server to start.
3. Start Node.js server
    ```bash
    npm start
    ```

### Running mobile application  

Mobile application was written in Kotlin and UI was built using Android's recommended toolkit for building native UI's - **Jetpack Compose**. 

1. Firstly, you''ll need to install Android Studio
    ```bash
    https://developer.android.com/studio
    ```
2. Then, you''ll need to install Android's emulator, I recommend:  
    ```bash
    Pixel_3a_API_34
    ```
3. Run the application by pressing on run button