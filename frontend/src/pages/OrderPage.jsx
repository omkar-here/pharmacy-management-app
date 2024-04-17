// import { Table } from "flowbite-react";
import { useCallback, useState } from "react";
// import { TableComponent } from "../components/TableComponent";
import useAxiosAuth from "../customHook/useAxiosAuth";
import SearchBar from "../components/Others/SearchBar";
export default function OrderPage() {
  const api = useAxiosAuth();
  const [search, setSearch] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [order, setOrder] = useState({
    customerId: "",
    employeeId: 0,
    type: "OUTGOING",
  });

  const handleSearch = useCallback(
    async (value) => {
      console.log(value);
      if (value?.length > 3) {
        try {
          const response = await api.get(`/medicine/search?name=${value}`);
          console.log(response.data);
          setSearchResults(response.data.data);
        } catch (error) {
          console.error(error);
        }
      }
    },
    [api]
  );

  const handleSelect = (value) => {
    setSearch(value);
  };

  return (
    <div className="grid grid-cols-2 ">
      {/* <SearchBar /> */}
      {/* <ClientPage /> */}
      <div className="flex flex-col gap-6 mt-7 w-full">
        <SearchBar
          searchResults={searchResults}
          setSearchResults={setSearchResults}
          handleSearch={handleSearch}
          handleSelect={handleSelect}
          setSearch={setSearch}
          search={search}
        />
        {/* <TableComponent data={searchResults} /> */}
      </div>
    </div>
  );
}
