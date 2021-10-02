import { Container } from '@mui/material';
import { Box } from '@mui/system';
import React, { useEffect, useState } from 'react';
import Table from './table';

export default function History() {
    const [records, setRecords] = useState([]);

    useEffect(() => {
        fetch("/api/history/all")
            .then(res => res.json())
            .then(res => {
                setRecords(res.records);
            })
            .catch(error => alert("Failed to load history"))
    }, []);

    return (
        <React.Fragment>
            <Container maxWidth="false">
                <Box sx={{ height: 10 }} />
                <Table rows={records} />
            </Container>
        </React.Fragment>
    );
}