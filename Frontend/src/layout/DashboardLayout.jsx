import { useUser } from "@clerk/react";
import SideMenu from "../components/SideMenu";
import Navbar from "../components/Navbar";

const DashboardLayout = ({ children, activeMenu }) => {
  const { user } = useUser();
  return (
    <div>
      {/* Navbar component goes here */}
      <Navbar activeMenu={activeMenu}/>
      {user && (
        <div className="flex">
          <div className="max-[1080px]:hidden">
            {/* Side menu goes here */}
            <SideMenu activeMenu={activeMenu}/>
          </div>
          <div className="grow mx-5">{children}</div>
        </div>
      )}
    </div>
  );
};

export default DashboardLayout; 