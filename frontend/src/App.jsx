import { useState } from "react";
import { Route, Routes, BrowserRouter } from "react-router-dom";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import "./App.css";
// FOLLOWING ARE THE CLIENTS AND EMPLOYEES PAGE
// TO BE ADDED ONTO DIFFERENT ROUTES
import AddClient from "./components/Client/AddClient";
import ShowClients from "./components/Client/ShowClients";
import ShowEmployees from "./components/Employee/ShowEmployees";
import AddEmployee from "./components/Employee/AddEmployee";
import AddMedicine from "./components/Medicine/AddMedicine";
import ShowMedicine from "./components/Medicine/ShowMedcines";
import HomeLayout from "./pages/HomeLayout";
import OrderPage from "./pages/OrderPage";
import Login from "./pages/Login";
import Register from "./pages/Register";

// function App() {
// 	const [show, setShow] = useState(true);

// 	return (
// 		<div className="container">
// 			<Stack spacing={2} direction="row">
// 				<Button variant="outlined" onClick={() => setShow(false)}>
// 					Add Client
// 				</Button>
// 				<Button variant="outlined" onClick={() => setShow(true)}>
// 					All Clients
// 				</Button>
// 			</Stack>

// 			{!show && <AddClient />}

// 			{show && <ShowClient />}
// 		</div>
// 	);
// }

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<HomeLayout />} />
          <Route index element={<Register />} />
          <Route path="/login" element={<Login />} />
          {/* <Route index element={<LandingPage />} />       */}
          <Route path="/order" element={<OrderPage />}>
            {/* <Route path="/:id" element={<OrderDetails />} /> */}
          </Route>
          {/* <Route path="clients" element={<ClientPage />}> */}
          {/* <Route path="/add" element={<AddClient />} />
            <Route path="/:id" element={<ClientDetails        />} /> */}
          {/* </Route> */}
          {/* <Route path="employees" element={<EmployeePage />}>
            <Route path="/add" element={<AddEmployee />} />
            <Route path="/:id" element={<EmployeeDetails />} />
          </Route>
          <Route path="medicines" element={<MedicinePage />}>
            <Route path="/add" element={<AddMedicine />} />
            <Route path="/:id" element={<MedicineDetails />} /> */}
          {/* </Route> */}
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
