import { Button, Stack } from "@mui/material";
import { useState } from "react";
import AddEmployee from "../components/Employee/AddEmployee";
import ShowEmployees from "../components/Employee/ShowEmployees";

function EmployeePage() {
	const [show, setShow] = useState(false);
	return (
		<>
			<div className="container">
				<Stack spacing={2} direction="row">
					<Button variant="outlined" onClick={() => setShow(false)}>
						Add Employee
					</Button>
					<Button variant="outlined" onClick={() => setShow(true)}>
						All Employees
					</Button>
				</Stack>

				{!show && <AddEmployee />}

				{show && <ShowEmployees />}
			</div>
		</>
	);
}

export default EmployeePage;
