import { Grid, TextField, Button } from "@mui/material";
import { useState } from "react";
import axiosAuth from "../../customHook/useAxiosAuth.js"
// import Table from "@mui/material/Table";
// import TableBody from "@mui/material/TableBody";
// import TableCell from "@mui/material/TableCell";
// import TableContainer from "@mui/material/TableContainer";
// import TableHead from "@mui/material/TableHead";
// import TableRow from "@mui/material/TableRow";
// import Paper from "@mui/material/Paper";
// import { Button } from "@mui/material";

export default function AddEmployee() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [role, setRole] = useState("");

  const api = axiosAuth();

  const handleSubmit = async (event) => {
    event.preventDefault();

    const data = {
      username,
      password,
      name,
      role,
    };

    try {
      // const response = await fetch("http://localhost:5432/employee/add", {
      //   method: "POST",
      //   headers: {
      //     "Content-Type": "application/json",
      //   },
      //   body: JSON.stringify(data),
      // });

      const response = await api.post("/employee/add", data);

      if (response.ok) {
        // Handle success
        console.log("Employee added successfully");
      } else {
        // Handle error
        console.error("Failed to add client");
      }
    } catch (error) {
      console.error("An error occurred", error);
    }
  };

  return (
    <Grid className="container" container spacing={2}>
      <Grid item xs={6}>
        {/* Image */}
        <img
          className="clientIcon"
          src="./public/icon.png"
          alt="Employee Image"
        />
      </Grid>
      <Grid item xs={6}>
        {/* Container */}
        <Grid container direction="column" spacing={2}>
          <Grid item>
            <form onSubmit={handleSubmit}>
              <TextField
                name="username"
                label="Username"
                fullWidth
                value={username}
                onChange={(event) => setUsername(event.target.value)}
              />
            </form>
          </Grid>
          <Grid item>
            <form onSubmit={handleSubmit}>
              <TextField
                name="password"
                label="Password"
                fullWidth
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
            </form>
          </Grid>
          <Grid item>
            <form onSubmit={handleSubmit}>
              <TextField
                name="name"
                label="Name"
                fullWidth
                value={name}
                onChange={(event) => setName(event.target.value)}
              />
            </form>
          </Grid>
          <Grid item>
            <form onSubmit={handleSubmit}>
              <TextField
                name="role"
                label="Role"
                fullWidth
                value={role}
                onChange={(event) => setRole(event.target.value)}
              />
            </form>
          </Grid>
          <Grid item>
            <Button variant="contained" color="primary" onClick={handleSubmit}>
              Add Employee
            </Button>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
}
