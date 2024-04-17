import { useState } from "react";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import "./App.css";
// FOLLOWING ARE THE CLIENTS AND EMPLOYEES PAGE
// TO BE ADDED ONTO DIFFERENT ROUTES
// import AddClient from "./components/Client/AddClient";
// import ShowClients from "./components/Client/ShowClients";
// import ShowEmployees from "./components/Employee/ShowEmployees";
// import AddEmployee from "./components/Employee/AddEmployee";
import AddMedicine from "./components/Medicine/AddMedicine";
import ShowMedicine from "./components/Medicine/ShowMedcines";
import Navbar from "./components/Navbar";

function App() {
	const [show, setShow] = useState(true);

	return (
		<div className="container">
			<Navbar />
			<Stack spacing={2} direction="row">
				<Button variant="outlined" onClick={() => setShow(false)}>
					Add Client
				</Button>
				<Button variant="outlined" onClick={() => setShow(true)}>
					All Clients
				</Button>
			</Stack>

			{!show && <AddMedicine />}

			{show && <ShowMedicine />}
		</div>
	);
}

export default App;
