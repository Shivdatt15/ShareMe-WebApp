import { BrowserRouter, Route, Routes } from "react-router-dom";
import { RedirectToSignIn, Show } from "@clerk/react";
import Landing from "./pages/Landing";
import Dashboard from "./pages/Dashboard";
import  Upload  from "./pages/Upload";
import MyFiles from "./pages/MyFiles";
import { Toaster } from "react-hot-toast";
import { UserCreditsProvider } from "./context/UserCreditsContext";
import PublicFileView from "./pages/PublicFileView";

const App= () => {
  return(
    <UserCreditsProvider>

    
            <BrowserRouter>
              <Toaster />

            <Routes>
                <Route path="/" element={<Landing />}/>
                <Route path="/dashboard" element={
                  <>
                     <Show when="signed-in"><Dashboard /></Show>
                     <Show when="signed-out"><RedirectToSignIn /></Show>

                  </>
                }/>
                <Route path="/upload" element={ <>
                     <Show when="signed-in"><Upload/></Show>
                     <Show when="signed-out"><RedirectToSignIn /></Show>
                  </>}/>
                <Route path="/my-files" element={ <>
                     <Show when="signed-in"><MyFiles/></Show>
                     <Show when="signed-out"><RedirectToSignIn /></Show>
                  </>}/>
            
                <Route path="file/:fileId" element={<PublicFileView/>}/>
                <Route path="/*" element={<RedirectToSignIn/>}/>
            </Routes>
            </BrowserRouter>  
      </UserCreditsProvider>                 
  )
}

export default App;