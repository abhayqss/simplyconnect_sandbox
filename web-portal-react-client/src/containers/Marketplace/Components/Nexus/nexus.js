import { Breadcrumbs } from "../../../../components";
import React from "react";
import './nexus.scss';
import NexusList from "./NexusList";
import Footer from "../../../../components/Footer/Footer";


const Nexus = () => {

  return (
    <div className='NexusWrap'>
      <Breadcrumbs items={[
        { title: 'Marketplace', href: '/marketplace', isEnabled: true },
        { title: 'Simply Nexus', href: `/marketplace/simplyNexus`, isActive: true },
      ]}/>


      <NexusList/>

      <Footer theme="gray" className='markplaceFooter'/>
    </div>
  )
};

export default Nexus;
