import { Button, Stack } from "@mui/material";
import { useState } from "react";
import AddClient from "../components/Client/AddClient";
import ShowClients from "../components/Client/ShowClients";

function ClientPage() {
	const [show, setShow] = useState(true);
	return (
		<>
			<div className="container">
				<Stack spacing={2} direction="row">
					<Button variant="outlined" onClick={() => setShow(false)}>
						Add Client
					</Button>
					<Button variant="outlined" onClick={() => setShow(true)}>
						All Clients
					</Button>
				</Stack>

				{!show && <AddClient />}

				{show && <ShowClients />}
			</div>
		</>
	);
}

export default ClientPage;
