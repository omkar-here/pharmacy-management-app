import {
	AppBar,
	Toolbar,
	IconButton,
	Typography,
	Stack,
	Button,
} from "@mui/material";
function Navbar() {
	return (
		<>
			<AppBar position="static">
				<Toolbar>
					<IconButton href="/">MA</IconButton>
					<Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
						MedApp
					</Typography>
					<Stack direction="row" spacing={2}>
						<Button style={{ color: "white" }} href="/clients">
							Clients
						</Button>
						<Button style={{ color: "white" }} href="/order">
							Orders
						</Button>
						<Button style={{ color: "white" }} href="/employees">
							Employees
						</Button>
						<Button style={{ color: "white" }} href="/register">
							Register
						</Button>
					</Stack>
				</Toolbar>
			</AppBar>
		</>
	);
}

export default Navbar;
