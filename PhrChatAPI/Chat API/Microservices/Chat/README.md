# notify-chat
a replacement Micro-service for Notify Messaging

# [Design Schema](https://github.com/eldermark/notify-server/issues/92#issuecomment-304974391)
![Design Schema](https://user-images.githubusercontent.com/13859670/29469537-544a3fa6-840e-11e7-86d1-03c1efe06e39.png)

## Service Setup
These dependencies must be installed globally as the initial step to configure the project environment.
- nodemon
- forever
- sequelize-cli

### Step 1:
`npm i -g nodemon@1.11.0 forever@0.15.3 sequelize-cli@2.7.0`

### Step 2:
The rest of the dependencies of this project and the initial pre-configuration will be installed running the command:
`npm i`

### Step 3:
provide this project with your own Environment variables in the following files:
- `.env` (Application Server Config)
- `/src/config/db.json` (db connection settings from an _empty_ existing db)
- `/src/config/tenantMaster.json` (Set Credentials for Company Migrator User )

### Step 4:
Run initial population of database by running these following commands in the console:
- `sequelize db:migrate`
- `sequelize db:seed:all`

### STEP 5 :
Check that there is no error when starting this service, running the `npm run dev` command, if everything was correct, stop the service (CTRL + C) and proceed to the final step.

### STEP 6 :
start Messaging2.0 micro-service by running in console `npm run prod`.

## Service Factory Reset
If for some reason you need to "clean" or "return to the start state" this service you need to follow these steps:
- run the command `sequelize db:migrate:undo:all`.
- Follow the steps described in the "Service Setup" section of this README file.

## Start Service
This project has the following commands for the Command Line Interface.

### `npm` Commands
- `npm start` Node Js starts the server as normal from the console.
- `npm run dev` Start the server in watcher mode using the _"nodemon"_ module.
- `npm run prod` Start the server as a service using the _"forever"_ module.
