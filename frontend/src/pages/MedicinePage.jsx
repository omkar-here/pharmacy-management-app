import { Button, Stack } from "@mui/material";
import { useState } from "react";
import AddClient from "../components/Client/AddClient";
import ShowClients from "../components/Client/ShowClients";
import ShowOrders from "../components/Order/ShowOrders";
import AddOrder from "../components/Order/AddOrder";
import AddMedicine from "../components/Medicine/AddMedicine";
import ShowMedicine from "../components/Medicine/ShowMedicines";

function MedicinePage() {
  const [show, setShow] = useState(true);
  return (
    <>
      <div className="container">
        <Stack spacing={2} direction="row">
          <Button variant="outlined" onClick={() => setShow(false)}>
            Add Medicine
          </Button>
          <Button variant="outlined" onClick={() => setShow(true)}>
            All Medicines
          </Button>
        </Stack>

        {!show && <AddMedicine />}

        {show && <ShowMedicine />}
      </div>
    </>
  );
}

export default MedicinePage;
