import React, { useState } from 'react';
import Editor from '@monaco-editor/react';
import { useRef } from 'react';
import {
    Button,
    Card,
    Container,
    createTheme,
    Grid,
    TextField,
    ThemeProvider,
    Typography
} from '@mui/material';
import { Box } from '@mui/system';

const theme = createTheme({
    typography: {
        fontFamily: [
            'sans-serif',
            '-apple-system',
            'BlinkMacSystemFont',
            '"Segoe UI"',
            'Roboto',
            '"Helvetica Neue"',
            'Arial',
            '"Apple Color Emoji"',
            '"Segoe UI Emoji"',
            '"Segoe UI Symbol"',
        ].join(','),
    },
});

export default function ScriptEditor(props) {
    const { fileName, content, commitFile, closeEditor } = props;
    const editorRef = useRef(null);
    const [message, setMessage] = useState("");

    const extractModelRef = (editor, monaco) => {
        editorRef.current = editor;
    }

    const handleOnClick = (event) => {
        let newContent = editorRef.current.getValue();
        commitFile(fileName, newContent, message)
            .then(() => closeEditor());
    }

    const handleOnChange = (event) => {
        setMessage(event.target.value);
    }

    const handleCancel = () => {
        closeEditor();
    }

    return (
        <React.Fragment>
            <Container>
                <Box sx={{ height: 20 }} />
                <Card variant="outlined" style={{
                    paddingLeft: 20,
                    paddingRight: 20,
                    paddingTop: 5,
                    paddingBottom: 3,
                    overflow: 'auto',
                    backgroundColor: "#0000009f",
                    borderTopLeftRadius: 3,
                    borderTopRightRadius: 3,
                    borderBottomLeftRadius: 0,
                    borderBottomRightRadius: 0,
                    borderWidth: 0,
                    display: 'inline-block'
                }}>
                    <ThemeProvider theme={theme} >
                        <Typography variant="h6" color="#eeeeee" component="div">{fileName}</Typography>
                    </ThemeProvider>
                </Card>
                <Editor
                    height="80vh"
                    theme="vs-dark"
                    defaultLanguage="javascript"
                    defaultValue={content}
                    onMount={extractModelRef}
                    options={{ fontSize: 15 }}
                />
                <Box sx={{ height: 20 }} />
                <TextField fullWidth label="Commit message" value={message} onChange={handleOnChange} />
                <Box sx={{ height: 10 }} />
                <Grid container justifyContent="flex-end">
                    <Button onClick={handleCancel} color="error" variant="outlined" size="">Cancel</Button>
                    &nbsp;&nbsp;
                    <Button onClick={handleOnClick} variant="contained" size="">Commit</Button>
                </Grid>
            </Container>
        </React.Fragment>
    );
}