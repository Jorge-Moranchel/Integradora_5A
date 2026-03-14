import React from 'react';

export default function StatCard({ title, value, subText, icon }) {
    return (
        <div className="card shadow-sm border-0 rounded-4 p-4 h-100">
            <div className="d-flex justify-content-between">
                <div>
                    <p className="text-muted small fw-bold mb-1">{title}</p>
                    <h2 className="fw-bold m-0">{value}</h2>
                    <small className="text-muted">{subText}</small>
                </div>
                <div className="bg-light p-3 rounded-4">{icon}</div>
            </div>
        </div>
    );
}