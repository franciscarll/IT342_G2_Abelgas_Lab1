import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService, userService } from '../services/api';
import '../styles/Dashboard.css';

function Dashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    userId: '',
    username: '',
    firstName: '',
    lastName: '',
  });

  useEffect(() => {
    loadUserData();
  }, []);

  const loadUserData = async () => {
    try {
      setLoading(true);
      const currentUser = authService.getCurrentUser();
      setUser(currentUser);

      // Fetch full profile from backend
      const response = await userService.getProfile();
      if (response.success) {
        setProfile(response.data);
        setFormData({
          userId: response.data.userId,
          username: response.data.username,
          firstName: response.data.firstName,
          lastName: response.data.lastName,
        });
      }
    } catch (err) {
      setError('Failed to load profile data');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    await authService.logout();
    navigate('/login');
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await userService.updateProfile(formData);
      if (response.success) {
        alert('Profile updated successfully!');
        setIsEditing(false);
        loadUserData(); // Reload profile data
      } else {
        setError(response.message || 'Update failed');
      }
    } catch (err) {
      setError(err.message || 'Failed to update profile');
    }
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="loading">Loading...</div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <nav className="dashboard-nav">
        <div className="nav-brand">
          <h2>User Dashboard</h2>
        </div>
        <button onClick={handleLogout} className="logout-button">
          Logout
        </button>
      </nav>

      <div className="dashboard-content">
        <div className="welcome-section">
          <h1>Welcome, {profile?.firstName}! 👋</h1>
          <p>Manage your profile and account settings</p>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="profile-card">
          <div className="card-header">
            <h3>Profile Information</h3>
            {!isEditing && (
              <button
                onClick={() => setIsEditing(true)}
                className="edit-button"
              >
                Edit Profile
              </button>
            )}
          </div>

          {!isEditing ? (
            <div className="profile-display">
              <div className="profile-item">
                <label>Username</label>
                <p>{profile?.username}</p>
              </div>
              <div className="profile-item">
                <label>Email</label>
                <p>{profile?.email}</p>
              </div>
              <div className="profile-item">
                <label>First Name</label>
                <p>{profile?.firstName}</p>
              </div>
              <div className="profile-item">
                <label>Last Name</label>
                <p>{profile?.lastName}</p>
              </div>
              <div className="profile-item">
                <label>Role</label>
                <p className="badge">{profile?.role}</p>
              </div>
              <div className="profile-item">
                <label>Account Status</label>
                <p className={profile?.isActive ? 'status-active' : 'status-inactive'}>
                  {profile?.isActive ? 'Active' : 'Inactive'}
                </p>
              </div>
              <div className="profile-item">
                <label>Member Since</label>
                <p>{profile?.createdAt ? new Date(profile.createdAt).toLocaleDateString() : 'N/A'}</p>
              </div>
              <div className="profile-item">
                <label>Last Login</label>
                <p>{profile?.lastLogin ? new Date(profile.lastLogin).toLocaleString() : 'N/A'}</p>
              </div>
            </div>
          ) : (
            <form onSubmit={handleUpdateProfile} className="profile-form">
              <div className="form-group">
                <label htmlFor="username">Username</label>
                <input
                  type="text"
                  id="username"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  required
                  minLength="3"
                />
              </div>

              <div className="form-group">
                <label htmlFor="firstName">First Name</label>
                <input
                  type="text"
                  id="firstName"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="lastName">Last Name</label>
                <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-actions">
                <button type="submit" className="save-button">
                  Save Changes
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setIsEditing(false);
                    setError('');
                  }}
                  className="cancel-button"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;