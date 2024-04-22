import { useState, useCallback } from "react";
import { IoSearch } from "react-icons/io5";
// import 
// "react-select-search/style.css";
import useAuth from "../../customHook/useAxiosAuth.js";
// import { TableComponent } from "./TableComponent.jsx";

export default function SearchBar({
  handleSearch,
  search,
  setSearch,
  searchResults,
  setSearchResults,
}) {
  const handleSubmit = useCallback(
    (e) => {
      e.preventDefault();
      handleSearch(search);
    },
    [handleSearch, search]
  );

  return (
    <div className="">
      <form
        onSubmit={handleSubmit}
        className="flex items-center max-w-sm mx-auto"
      >
        <label htmlFor="simple-search" className="sr-only">
          Search
        </label>
        <div className="relative w-full">
          <div className="absolute inset-y-0 start-0 flex items-center ps-3 pointer-events-none">
            <IoSearch className="w-4 h-4 text-gray-500 dark:text-gray-400" />
          </div>
          <input
            onChange={(e) => {
              console.log(e.target.value);
              setSearch(e.target.value);
            }}
            value={search}
            type="text"
            id="simple-search"
            className="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full ps-10 p-2.5  dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
            placeholder="Search branch name..."
            required
          />
        </div>
        <button
          type="submit"
          className="p-2.5 ms-2 text-sm font-medium text-white bg-blue-700 rounded-lg border border-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
        >
          <IoSearch className="w-4 h-4" />
          <span className="sr-only">Search</span>
        </button>
      </form>
    </div>
  );
}
