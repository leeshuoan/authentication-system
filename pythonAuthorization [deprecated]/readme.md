# Quick start

### Start Python virtual environment
```bash
python -m venv venv
```
### Activate virtual environment
```bash
source venv/bin/activate
```
### Install dependencies
```bash
pip install -r requirements.txt
```
### Prepare necessary variables in config.json
```json
{
    "client_id": "YOUR_CLIENT_ID",
    "client_secret": "YOUR_CLIENT_SECRET",
    "scopes": "YOUR_SCOPES",
    "response_type": "YOUR_RESPONSE_TYPE",
    "bank_end_point": "YOUR_BANK_END_POINT",
    "redirect_uri": "YOUR_REDIRECT_URI",
    "session_secret_key": "YOUR_SESSION_SECRET_KEY",
}
```
### Run the app
```bash
python app.py
```


# Notes:
The current app works as a monolith
! To integrate with other apps and this should serve only as an endpoint for the other apps to get the access token