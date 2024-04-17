import { Outlet } from "react-router-dom";

// import {Outlet} from "react-router-dom";
export default function LayoutPage() {
  return (
    <div>
      <h1>Home Layout Page</h1>{" "}
      <Outlet />
    </div>
  );
}
