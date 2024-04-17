import {
  Grid,
  TextField,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
} from "@mui/material";
import { useState } from "react";

export default function AddOrder() {
  const [name, setName] = useState("");
  const [brand, setBrand] = useState("");
  const [type, setType] = useState("");

  const handleSubmit = async (event) => {
    event.preventDefault();

    const data = {
      name,
      brand,
      type,
    };

    try {
      const response = await fetch("http://localhost:5432/medicine/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        console.log("Medicine added successfully");
      } else {
        console.error("Failed to add medicine");
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
          className="medicineIcon"
          src="./public/icon.png"
          alt="Medicine Image"
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
                name="brand"
                label="Brand"
                fullWidth
                value={brand}
                onChange={(event) => setBrand(event.target.value)}
              />
            </form>
          </Grid>
          <Grid item>
            <FormControl fullWidth>
              <InputLabel>Type</InputLabel>
              <Select
                value={type}
                onChange={(event) => setType(event.target.value)}
              >
                <MenuItem value="syrup">Syrup</MenuItem>
                <MenuItem value="tablets">Tablets</MenuItem>
                <MenuItem value="capsules">Capsules</MenuItem>
                <MenuItem value="pills">Pills</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item>
            <Button variant="contained" color="primary" onClick={handleSubmit}>
              Add Medicine
            </Button>
          </Grid>
        </Grid>
      </Grid>
    </Grid>
  );
}
