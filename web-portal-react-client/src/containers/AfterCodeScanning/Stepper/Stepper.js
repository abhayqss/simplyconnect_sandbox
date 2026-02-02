import React from 'react';
import './Stepper.scss';

const steps = [
  { label: 'Vendor Information' },
  { label: 'Account Information' },
  { label: 'Complete Registration' },
];

const Stepper = ({ currentStep }) => {
  return (
    <ol className="steps">
      <li className="done">1</li>
      <li className="done">2</li>
      <li className="done">3</li>
    </ol>
  );
};

export default Stepper;
