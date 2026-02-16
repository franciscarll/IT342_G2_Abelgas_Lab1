import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './Profile.css';

function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const token = localStorage.getItem('token');
      
      if (!token) {
        // Fallback to localStorage user if no API call needed
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        setProfile(user);
        setLoading(false);
        return;
      }

      // Fetch from backend
      const response = await axios.get('http://localhost:8080/api/user/me', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.data.success) {
        setProfile(response.data.data);
      } else {
        // Fallback to localStorage
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        setProfile(user);
      }
    } catch (err) {
      console.error('Error fetching profile:', err);
      // Fallback to localStorage on error
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      setProfile(user);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="profile-content">
          <div className="loading">Loading...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="profile-content">
        <div className="profile-card">
          <div className="profile-header">
            <div className="profile-avatar">
              {profile?.firstName?.charAt(0) || profile?.username?.charAt(0) || 'U'}
            </div>
            <h2 className="profile-name">
              {profile?.firstName && profile?.lastName 
                ? `${profile.firstName} ${profile.lastName}`
                : profile?.username || 'User'}
            </h2>
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className="profile-details">
            <div className="detail-item">
              <label>Username</label>
              <p>{profile?.username || 'N/A'}</p>
            </div>

            <div className="detail-item">
              <label>Email</label>
              <p>{profile?.email || 'N/A'}</p>
            </div>

            {profile?.firstName && (
              <div className="detail-item">
                <label>First Name</label>
                <p>{profile.firstName}</p>
              </div>
            )}

            {profile?.lastName && (
              <div className="detail-item">
                <label>Last Name</label>
                <p>{profile.lastName}</p>
              </div>
            )}

            {profile?.role && (
              <div className="detail-item">
                <label>Role</label>
                <p className="role-badge">{profile.role}</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile;