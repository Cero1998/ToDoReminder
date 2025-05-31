from flask import Flask, request, jsonify
from flask_cors import CORS
from pymongo import MongoClient
import bcrypt

app = Flask(__name__)
CORS(app)

client = MongoClient("mongodb://localhost:27017/")
db = client.todo_db

@app.route("/register", methods=["POST"])
def register():
    data = request.get_json()
    email = data["email"]
    password = data["password"].encode('utf-8')
    
    if db.users.find_one({"email": email}):
        return jsonify({"message":"","userId":"","error": "User already exists","success": False})

    hashed = bcrypt.hashpw(password, bcrypt.gensalt())
    db.users.insert_one({"email": email, "password": hashed})
    return jsonify({"message": "User registered successfully","userId":"","error": "","success": True})

@app.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    user = db.users.find_one({"email": data["email"]})

    if user and bcrypt.checkpw(data["password"].encode('utf-8'), user["password"]):
        return jsonify({"message": "Login successful", "userId": str(user["_id"]),"error": "","success": True})
    elif not user:
        return jsonify({"message": "", "userId": "","error": "User not found","success": False})
    return jsonify({"message":"", "userId":"", "error": "Invalid credentials", "success": False})





if __name__ == "__main__":
    app.run(debug=True)
