import { useContext, useEffect, useState } from "react";
import { Menu, Wallet, X } from "lucide-react";
import { UserButton } from "@clerk/react";
import { Link } from "react-router-dom";
import SideMenu from "./SideMenu";
import CreditsDisplay from "./CreditsDisplay";
import {UserCreditsContext} from "../context/UserCreditsContext"


const Navbar = ({ activeMenu }) => {
  const [openSideMenu, setOpenSideMenu] = useState(false);
  const { credits, fetchUserCredits } = useContext(UserCreditsContext);

useEffect(()=> {
    fetchUserCredits();
}, [fetchUserCredits]);


  return (
    <div className="flex items-center justify-between gap-5 bg-white border border-b border-gray-200/50 backdrop-blur-[2px] py-4 px-4 sm:px-7 sticky top-0 z-30">
      
      {/* Left side */}
      <div className="flex items-center gap-5">
        <button
          onClick={() => setOpenSideMenu(!openSideMenu)}
          className="block lg:hidden text-black hover:bg-gray-100 p-1 rounded transition-colors"
        >
          {openSideMenu ? (
            <X className="text-2xl" />
          ) : (
            <Menu className="text-2xl" />
          )}
        </button>

        <div className="flex items-center gap-2">
          <img
  src="/sharemelogo.jpg"
  alt="ShareMe logo"
  className="w-8 h-8 object-contain"
/>


          <span className="text-lg font-medium text-black truncate">
            ShareMe
          </span>
        </div>
      </div>

      {/* Right side */}
      
      <div className="flex items-center gap-4">
          <CreditsDisplay credits={credits} />
        

        <div className="relative">
          <UserButton />
        </div>
      </div>

      {/* Mobile side menu */}
      {openSideMenu && (
        <div className="fixed top-[73px] left-0 right-0 bg-white border-b border-gray-200 lg:hidden z-20">
          <SideMenu activeMenu={activeMenu} />
        </div>
      )}
    </div>
  );
};

export default Navbar;
