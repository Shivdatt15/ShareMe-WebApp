import { Files, LayoutDashboard, Upload } from "lucide-react";

const features = [
	{
		icon: "ArrowUpCircle",
		iconColor: "#a855f7",
		title: "Easy File Upload",
		description:
			"Quickly upload your files with our intuitive drag-and-drop interface.",
	},
	{
		icon: "Shield",
		iconColor: "#22c55e",
		title: "Secure Storage",
		description:
			"Your files are encrypted and stored securely in our cloud infrastructure.",
	},
	{
		icon: "Share2",
		iconColor: "#a855f7",
		title: "Simple Sharing",
		description:
			"Share files with anyone using secure links that you control.",
	},
	
	{
		icon: "FileText",
		iconColor: "#ef4444",
		title: "File Management",
		description:
			"Organize, preview, and manage your files from any device.",
	},
	
];

//side menu 
export const SIDE_MENU_DATA= [
  {
    id: "01",
    label: "Dashboard",
    icon: LayoutDashboard,
    path: "/dashboard",
  },
  {
    id: "02",
    label: "Upload",
    icon: Upload,
    path: "/upload",
  },
  {
    id: "03",
    label: "My Files",
    icon: Files,
    path: "/my-files",
  }
];


export { features };
export default features;
