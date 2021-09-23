import React, { useEffect, useState } from 'react';
import Table from './table';

/* TODO: Build complete page */
export default function History() {
    const [records, setRecords] = useState([]);

    useEffect(() => {
        fetch("http://localhost:8080/history/all")
            .then(res => res.json())
            .then(res => {
                console.log(res.records);
                setRecords(res.records);
            })
    }, []);

    return <Table rows={records} />;
}