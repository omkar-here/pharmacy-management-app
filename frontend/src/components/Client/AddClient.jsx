import { Grid, TextField, Button } from "@mui/material";
import { useState } from "react";
import useAxiosAuth from "../../customHook/useAxiosAuth.js";
// import Table from "@mui/material/Table";
// import TableBody from "@mui/material/TableBody";
// import TableCell from "@mui/material/TableCell";
// import TableContainer from "@mui/material/TableContainer";
// import TableHead from "@mui/material/TableHead";
// import TableRow from "@mui/material/TableRow";
// import Paper from "@mui/material/Paper";
// import { Button } from "@mui/material";

export default function AddClient() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [address, setAddress] = useState("");

  const api = useAxiosAuth();

  const handleSubmit = async (event) => {
    event.preventDefault();

    const data = {
      name,
      email,
      phone,
      address,
    };

    try {
      // const response = await fetch("http://localhost:5432/client/add", {
      //   method: "POST",
      //   headers: {
      //     "Content-Type": "application/json",
      //   },
      //   body: JSON.stringify(data),
      // });

      const response = await api.post("/client/add", data);

      if (response.ok) {
        // Handle success
        console.log("Client added successfully");
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
          alt="Client Image"
        />
      </Grid>
      <Grid item xs={6}>
        {/* Container */}
        <Grid container direction="column" spacing={2}>
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
                name="email"
                label="Email"
                fullWidth
                value={email}
                onChange={(event) => setEmail(event.target.value)}
              />
            </form>
          </Grid>
          <Grid item>
            <form onSubmit={handleSubmit}>
              <TextField
                name="phone"
                label="Phone"
                fullWidth
                value={phone}
                onChange={(event) => setPhone(event.target.value)}
              />
            </form>
          </Grid>
          <Grid item>
            <form onSubmit={handleSubmit}>
              <TextField
                name="address"
                label="Address"
                fullWidth
                value={address}
                onChange={(event) => setAddress(event.target.value)}
              />
            </form>
          </Grid>
          <Grid item>
            <Button variant="contained" color="primary" onClick={handleSubmit}>
              Add Client
            </Button>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
}
