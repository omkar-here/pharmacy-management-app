import { Outlet } from "react-router-dom";
import doctors from "../assets/doctors.png";
import wave from "../assets/wave.png";
import { Box, Button, Typography } from "@mui/material";
// import {Outlet} from "react-router-dom";
export default function HomeLayout() {
	return (
		<div>
			<Box
				sx={{
					display: "flex",
				}}>
				<img src={doctors} alt="doctors" />
				<Box
					sx={{
						display: "flex",
						flexDirection: "column",
						justifyContent: "center",
						alignItems: "center",
					}}>
					<Typography
						variant="h3"
						sx={{
							fontfamily: "Montserrat",
							fontWeight: "bold",
						}}
						gutterBottom>
						Welcome to <span style={{ color: "#053B3F" }}>MedApp</span>
					</Typography>
					<Typography
						variant="h5"
						sx={{
							fontWeight: "30",
						}}
						gutterBottom>
						Get your medicines anywhere anytime
					</Typography>
					<Button
						style={{
							backgroundColor: "#053B3F",
							color: "white",
							width: "100px",
						}}
						href="/register">
						Register
					</Button>
				</Box>
			</Box>
			<img
				style={{ position: "absolute", bottom: "0px", zIndex: "-1" ,width:"100%"}}
				src={wave}
				alt="noi"
			/>
			<Outlet />
		</div>
	);
}
