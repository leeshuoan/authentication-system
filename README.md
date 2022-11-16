# ITSA G2 T8
## Cloud Deployment
The main branch is used during our local development and for our client app.

In production, our server repository uses the branch `production-server`.

Visit our site [here](http://itsag2t8.com)

## Local development
### 🔧 Setting up
1️⃣ Clone our repository from GitHub to your `desired_folder_name`
```bash
git clone https://github.com/cs301-itsa/project-2022-23t1-project-2022-23t1-g2-t8.git desired_folder_name
```

2️⃣ You will have to set up an OAuth app in [http://smurnauth-production.fly.dev](http://smurnauth-production.fly.dev) with credentials:<br>
|username| password|
|--------|--------|
|admin@example.com| admin_password|

the redirect should http://localhost:5173/auth/callback
<br><br>
3️⃣ Edit the database password `spring.datasource.password` according to your system settings
```
~/project-2022-23t1-project-2022-23t1-g2-t8/server/src/main/resources/application.properties
```

#### 🏃 Running the application 
4️⃣ Open a new terminal and perform the commands below to install the required dependencies for the frontend and run the frontend app
```bash
cd ~/project-2022-23t1-project-2022-23t1-g2-t8/frontend-app
npm install 
npm run dev
```
<br>
5️⃣ Open another new terminal and and perform the commands to install Java and spring
```bash
cd ~/project-2022-23t1-project-2022-23t1-g2-t8/server
mvn spring-boot:run
```
<br>

## 6️⃣ Trying out our application
### ⚡️ Quick Start!
##### ❕ SSO Login
Select any user in the file that can be found in the excel sheet below:
`data/Project A - users.xlsx`<br>
The password is "password"

##### ❕ Hosted Login
You can log in as admins with the following users:
| role                    | username                 | password |
|-------------------------|--------------------------|----------|
| superadmin (read/write) | russel.stephan@kihn.name | password |
| admin (read only)       | pagac_vince@yost.io      | password |

##### ❕ Enrollment
You can enroll any user in the excel: `data/Project A - users.xlsx` except the two admins above.


## ✅ You can access the local webpage [here](http://localhost:5173/).


#### Optional - Installation guides
[Install MAMP](https://www.mamp.info/en/downloads/) for mac users
[install WAMP](https://www.wampserver.com/en/) for windows users

Open mamp or wamp and start the servers.
