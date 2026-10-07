import { useEffect } from "react";
import { useClerk, useAuth } from "@clerk/react";
import { useNavigate } from "react-router-dom";
import CTASection from "../components/landing/CTASection";
import FeaturesSection from "../components/landing/FeaturesSection";
import FooterSection from "../components/landing/FooterSection";
import HeroSection from "../components/landing/HeroSection";
import { features } from "../assets/data";

const Landing = () =>{

    const {openSignIn,openSignUp} = useClerk();
    const {isSignedIn} = useAuth();
    const navigate = useNavigate();

    useEffect(()=>{
        if(isSignedIn){
            navigate("/dashboard");
        }
    }, [isSignedIn,navigate]);

    return(
        <div className="landing-page bg-gradient-to-b from-gray-50 to-gray-100">
            {/* Hero Section */}
             <HeroSection openSignIn={openSignIn} openSignUp={openSignUp}/>

            {/* Features sect. */}
             <FeaturesSection features={features}/>

            {/* CTA secti */}
            <CTASection openSignUp={openSignUp}/>
            
            {/* Footer */}
            <FooterSection/>

        </div>    
    )
}

export default Landing;