import React, { useState } from 'react'
import './LoginSignup.css'
import user_icon from '../Assets/person.png'
import email_icon from '../Assets/email.png'
import password_icon from '../Assets/password.png'
import confirmpassword_icon from '../Assets/confirmpassword.png' 
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons' 



const LoginSignup = () => {

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const[action,setAction]=useState("Login");
  return (
    <div className='container'>
      <div className='header'>
        <div className='text'>{action}</div>
        <div className='underline'></div>
      </div>



      <div className='inputs'>
        {action==="Login"?<div></div>:<div className='input'>
          <img src={user_icon} alt='user' />
          <input type='text' placeholder='Name' />
        </div>}
        
       
        <div className='input'>
          <img src={email_icon} alt='email' />
          <input type='email' placeholder='Email' />
      </div>
      <div className='input'>
          <img src={user_icon} alt='role' />
          <select className="select-role" defaultValue="" required>
    <option value="" disabled hidden>Select Role</option>
    <option value="farmer">Farmer</option>
    <option value="consumer">Consumer</option>
  </select>
        </div>

      <div className='input'>
        <img src={password_icon} alt='password' />
        <input type={showPassword ? 'text' : 'password'} placeholder='Password'/>
        <FontAwesomeIcon
          icon={showPassword ? faEyeSlash : faEye}
          className="toggle-visibility"
          onClick={() => setShowPassword(!showPassword)}/>
      </div>

      {action==="Login"?<div></div>:
      <div className='input'>
      <img src={password_icon} alt='confirmpassword' />
      <input
        type={showConfirmPassword ? 'text' : 'password'}
        placeholder='Confirm Password'
      />
      <FontAwesomeIcon
  icon={showConfirmPassword ? faEyeSlash : faEye}
  className="toggle-visibility"
  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
/>
    </div>}
      
    </div>
    {action==="Sign Up"?<div></div>:<div className='forgot_password'>Forgot Password? <span>Click Here!</span></div>}
    
    <div className='submit_container'>
      <div className={action==="Login"?"submit gray":"submit"}onClick={()=>{setAction("Sign Up")}}> Sign Up</div>
      <div className={action==="Sign Up"?"submit gray":"submit"}onClick={()=>{setAction("Login")}}>Log In</div>

    </div>
    </div>
  )
}

export default LoginSignup
