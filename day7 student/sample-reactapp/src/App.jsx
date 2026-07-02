import { useState } from "react";
import "./App.css";

function App() {
  const [students, setStudents] = useState([]);
  const [formData, setFormData] = useState({
    name: "",
    className: "",
    rollNumber: "",
  });

  // Handle input changes
  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  // Add student
  const addStudent = () => {
    if (
      formData.name.trim() === "" ||
      formData.className.trim() === "" ||
      formData.rollNumber.trim() === ""
    ) {
      alert("Please fill in all fields!");
      return;
    }

    const newStudent = {
      id: Date.now(),
      name: formData.name,
      className: formData.className,
      rollNumber: formData.rollNumber,
      attendance: "Absent", // Default attendance
    };

    setStudents([...students, newStudent]);

    // Clear form
    setFormData({
      name: "",
      className: "",
      rollNumber: "",
    });
  };

  // Toggle attendance
  const toggleAttendance = (id) => {
    setStudents(
      students.map((student) =>
        student.id === id
          ? {
              ...student,
              attendance:
                student.attendance === "Absent" ? "Present" : "Absent",
            }
          : student
      )
    );
  };

  // Delete student
  const deleteStudent = (id) => {
    setStudents(students.filter((student) => student.id !== id));
  };

  return (
    <div className="container">
      <h1>🎓 Student Management System</h1>

      <div className="form">
        <input
          type="text"
          name="name"
          placeholder="Enter Student Name"
          value={formData.name}
          onChange={handleChange}
        />

        <input
          type="text"
          name="className"
          placeholder="Enter Class"
          value={formData.className}
          onChange={handleChange}
        />

        <input
          type="text"
          name="rollNumber"
          placeholder="Enter Roll Number"
          value={formData.rollNumber}
          onChange={handleChange}
        />

        <button onClick={addStudent}>Add Student</button>
      </div>

      <table>
        <thead>
          <tr>
            <th>Roll No</th>
            <th>Name</th>
            <th>Class</th>
            <th>Attendance</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {students.length === 0 ? (
            <tr>
              <td colSpan="5" style={{ textAlign: "center" }}>
                No students added.
              </td>
            </tr>
          ) : (
            students.map((student) => (
              <tr key={student.id}>
                <td>{student.rollNumber}</td>
                <td>{student.name}</td>
                <td>{student.className}</td>

                <td
                  style={{
                    color:
                      student.attendance === "Present"
                        ? "green"
                        : "red",
                    fontWeight: "bold",
                  }}
                >
                  {student.attendance}
                </td>

                <td>
                  <button
                    onClick={() => toggleAttendance(student.id)}
                    style={{
                      marginRight: "10px",
                    }}
                  >
                    {student.attendance === "Absent"
                      ? "Mark Present"
                      : "Mark Absent"}
                  </button>

                  <button onClick={() => deleteStudent(student.id)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default App;