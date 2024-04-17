import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import { Button } from "@mui/material";
import Typography from "@mui/material/Typography";
import { useEffect, useState } from "react";

export default function ShowOrders() {
  const [rows, setRows] = useState([]);
  const [refresh, setRefresh] = useState(false);
  function createData(medicine_id, name, brand, type) {
    return { medicine_id, name, brand, type };
  }

  useEffect(() => {
    fetch("http://localhost:5432/order/all", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => response.json())
      .then((data) => {
        setRows(
          data.data.map((medicine) =>
            createData(
              medicine.id,
              medicine.name,
              medicine.brand,
              medicine.type
            )
          )
        );
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  }, [refresh]);

  function handleDelete(medicineId) {
    fetch(`http://localhost:5432/medicine/${medicineId}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => response.json())
      .then(() => {
        setRefresh(!refresh);
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  }

  return (
    <>
      <div>
        <div className="container">
          <Typography component="h1" variant="h6" color="primary" gutterBottom>
            All Medicines
          </Typography>
          <div className="card-container">
            {/* here the searchbars will go  */}
          </div>
          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 650 }} aria-label="simple table">
              <TableHead>
                <TableRow>
                  <TableCell className="table-header">Medicine ID</TableCell>
                  <TableCell align="right" className="table-header">
                    Name
                  </TableCell>
                  <TableCell align="right" className="table-header">
                    Brand
                  </TableCell>
                  <TableCell align="right" className="table-header">
                    Type
                  </TableCell>
                  <TableCell align="right" className="table-header">
                    Actions
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {rows.map((row) => (
                  <TableRow
                    key={row.medicine_id}
                    sx={{
                      "&:last-child td, &:last-child th": { border: 0 },
                    }}
                  >
                    <TableCell
                      component="th"
                      scope="row"
                      className="table-body"
                    >
                      {row.medicine_id}
                    </TableCell>
                    <TableCell align="right" className="table-body">
                      {row.name}
                    </TableCell>
                    <TableCell align="right" className="table-body">
                      {row.brand}
                    </TableCell>
                    <TableCell align="right" className="table-body">
                      {row.type}
                    </TableCell>
                    <TableCell align="right" className="action-buttons">
                      <Button onClick={() => handleDelete(row.medicine_id)}>
                        Delete
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </div>
      </div>
    </>
  );
}
