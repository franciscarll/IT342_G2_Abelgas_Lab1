import React, { useEffect, useState } from 'react';
import './Dashboard.css';

function Dashboard() {
  const [userName, setUserName] = useState('');

  useEffect(() => {
    // Get user info from localStorage
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    setUserName(user.firstName || user.username || 'User');
  }, []);

  return (
    <div className="page-container">
      <div className="dashboard-content">
        <h1 className="welcome-title">Welcome, {userName}</h1>
      </div>
    </div>
  );
}

export default Dashboard;