from crypt import methods
import json
from flask import Flask, render_template, url_for, request, session, redirect
from flask_session import Session
import requests

app = Flask(__name__)
# config
config_file = json.load(open("config.json"))
client_id = config_file["client_id"]
client_secret = config_file["client_secret"]
scopes = config_file["scopes"]
response_type = config_file["response_type"]
bank_end_point = config_file["bank_end_point"]
redirect_uri = config_file["redirect_uri"]

app.config['SESSION_TYPE'] = 'filesystem'
app.config['SECRET_KEY'] = config_file["session_secret_key"]
Session(app)

@app.route('/')
def index():
    error_description = ""
    if request.args.get('error_description'):
        error_description = request.args.get('error_description')

    if session.get("token") == True:
        return redirect('/user')
    return render_template('index.html', error_description=error_description)

@app.route('/login')
def login():
    return redirect(f"{bank_end_point}/authorize?client_id={client_id}&response_type={response_type}&scope={scopes}&redirect_uri={redirect_uri}")
    
@app.route('/sso/auth/callback', methods=['GET'])
def callback():
    if request.args.get('error'):
        return redirect(url_for('index', error_description=request.args.get('error_description')))
    code = request.args.get('code')
    data = {
        'client_id': client_id,
        'client_secret': client_secret,
        'grant_type': 'authorization_code',
        'code': code,
        'redirect_uri': redirect_uri
    }
    response = requests.post(f"{bank_end_point}/token", data=data)
    print(response.json())
    print(response.status_code)
    if response.status_code == 200:
        session['token'] = response.json()['access_token']
        return response.json(),200
        # return redirect('/user')
    else:
        return redirect(url_for('index', error_description="Something went wrong!"))

@app.route('/user')
def user():
    print(session['token'])
    # if session.get("token") != True:
    #     return redirect(url_for('index', error_description="You are not logged in!"))
    return render_template("user.html")

@app.route('/logout')
def logout():
    response = requests.post(f"{bank_end_point}/users/sign_out", headers={'Authorization': f'Bearer {session["token"]}'})
    if response.status_code == 200:
        session.pop('token', None)
        return redirect(url_for('index', error_description="You are logged out!"))
    return redirect(url_for('index', error_description="You are not logged in!"))


@app.route('/profile')
def profile():
    # if session.get("token") != True:
    #     return redirect(url_for('index', error_description="You are not logged in!"))

    response = requests.get(f"{bank_end_point}/userinfo", headers={'Authorization': f'Bearer {session["token"]}'})
    print(response.json())
    return response.json()

if __name__ == '__main__':
    app.run(port=5000, debug=True)
