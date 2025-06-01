from flask import Flask, request, jsonify
from flask_cors import CORS
from pymongo import MongoClient
from bson import ObjectId
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


@app.route("/add_todo", methods=["POST"])
def add_todo():
    data = request.get_json()
    title = data.get("title")
    completed = data.get("completed", False)
    date = data.get("date")
    userId = data.get("userId")
    
    if not title or date is None:
        return jsonify({"error": "Missing fields"}), 400

    # Genera un ID incrementale univoco
    last = db.todos.find_one(sort=[("id", -1)])
    next_id = (last["id"] + 1) if last else 1

    todo = {
        "id": next_id,
        "title": title,
        "completed": completed,
        "date": date,
        "userId": userId
    }

    db.todos.insert_one(todo)
    return jsonify({"message": "Created new Todo", "id": next_id, "success": True, "error": ""})


@app.route("/todos/<user_id>", methods=["GET"])
def get_user_todos(user_id):
    todos = list(db.todos.find({"userId": user_id}))
    for todo in todos:
        todo["_id"] = str(todo["_id"])  # MongoDB ObjectId non è serializzabile
    return jsonify(todos)


@app.route("/todos/<int:todo_id>/complete", methods=["PUT"])
def complete_todo(todo_id):
    result = db.todos.update_one(
        {"id": todo_id},
        {"$set": {"completed": True}}
    )
    if result.matched_count == 0:
        return jsonify({"success": False, "message": "Todo not found"}), 404

    return jsonify({"success": True, "message": "Todo marked as completed"})



if __name__ == "__main__":
    app.run(debug=True)
