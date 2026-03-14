import React from 'react';

export default function Navbar({ title }) {
    return (
        <div className="d-flex justify-content-between align-items-center mb-4">
            <h2 className="fw-bold m-0">{title}</h2>
        </div>
    );
}