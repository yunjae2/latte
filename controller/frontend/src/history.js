import React, { useEffect, useState } from 'react';
import Table from './table';

/* TODO: Build complete page */
export default function History() {
    const [records, setRecords] = useState([]);

    /* TODO: Make the url customizable */
    useEffect(() => {
        fetch("http://localhost:8080/history/all")
            .then(res => res.json())
            .then(res => {
                setRecords(res.records);
            })
    }, []);

    return <Table rows={records} />;
}