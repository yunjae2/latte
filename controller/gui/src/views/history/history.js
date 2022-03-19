import { Container } from '@mui/material';
import { Box } from '@mui/system';
import React, { useEffect, useState } from 'react';
import Table from './table';

export default function History() {
    return (
        <React.Fragment>
            <Container maxWidth="false">
                <Box sx={{ height: 10 }} />
                <Table />
            </Container>
        </React.Fragment>
    );
}