# ITSA G2 T8
## Background
Authentication service with the following features
1. Enrolment service that lets customers enrol and also verifies ownership of accounts
2. User authentication via Hosted Login and SSO of partnered bank's secured with the signing and encryption of JWTs
3. Access Control Management through an admin panel to manage user roles and permissions
4. JWKS Endpoints to communicate with partner’s backend services 

## Architecture 
![ITSA g1t2-Detailed New Main Architecture drawio](https://user-images.githubusercontent.com/72493091/202087704-ff8e1b3e-e776-426b-8273-7b424ce88dca.png)

## CI/CD Pipeline
![ITSA g1t2-CICD CodePipeline drawio](https://user-images.githubusercontent.com/72493091/202087805-961efc7f-caf0-4ed7-9518-d4c37fcbc007.png)

## Quality Attributes
### Interoperability
Our application implements an authorization code flow in which the “response type” value is code which follows what is defined in OAuth 2.0 (RFC6749). We ensure interoperability with third-party or external applications as our JWT claims follow the registered JWT type where we will define the JWT specifications (RFC7519). We will be adhering closely to RFC2616 for the HTTP GET and POST methods in our authorization flow.
We request user consent to allow the authentication service to access users’ profile information. In our API gateway, we will configure a JWT authorizer for the API routes such that it will validate the JWTs that clients submit with API requests. The API Gateway will allow or deny requests based on the token validation.

### Resilience & Disaster Recovery
As part of our disaster recovery plan, we have decided on employing a pilot light standby disaster recovery strategy. The pilot light standby strategy deploys a functional stack but acts as a passive region only. When data centers in a region go down, Route 53 will perform region failover and be able to direct traffic to the passive region to take on the computational workload and handle the requests. If there is a within-region failure, our application load balancer will help to redirect traffic from one availability zone to the other. 

### Scalability 
Our authorization servers are hosted on EC2 instances organized into separate autoscaling groups which will allow us to easily scale out or in according to our minimum, desired and maximum capacity. Instances are spread across multiple AZs, in response to the computational workload, to maximize capacity. With RDS storage autoscaling, actual storage consumption will be monitored, and capacity will scale up automatically when actual utilization approaches provisioned storage capacity.  

### Data Security
#### Personal Information
Customer information in Amazon RDS is logically segregated so users and customers will not be able to access resources not assigned to them. Amazon RDS encrypts data with the keys that we manage with AWS Key Management Service (KMS). All data including the replicas, backups and snapshots are encrypted at rest. To effectively remove user PII, we will implement a “Delete Account” feature so that users will be able to completely delete their information from their database
#### Systems Security
Any findings will create a log in AWS Cloudwatch Events which can in turn trigger a message to AWS SNS to send notifications to the relevant stakeholders.
With AWS IAM, we will employ the principle of least privilege and ensure that users only get permissions for what they require, nothing more. With AWS WAF, our application is protected from common web exploits and bots as we can filter traffic based on rules that we have created. Using AWS VPC, we will create public and private subnets to ensure that instances in the private subnets are not accessible to the internet.

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
