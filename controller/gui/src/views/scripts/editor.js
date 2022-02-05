import React, { useState } from 'react';
import Editor from '@monaco-editor/react';
import { useRef } from 'react';
import { Button, Container, CssBaseline, Grid, TextField } from '@mui/material';
import { Box } from '@mui/system';

export default function ScriptEditor(props) {
    const { fileName, content, commitFile } = props;
    const editorRef = useRef(null);
    const [message, setMessage] = useState("");

    const extractModelRef = (editor, monaco) => {
        editorRef.current = editor;
    }

    const handleOnClick = (event) => {
        let newContent = editorRef.current.getValue();
        commitFile(fileName, newContent, message);
    }

    const handleOnChange = (event) => {
        setMessage(event.target.value);
    }

    return (
        <React.Fragment>
            <Container>
                <Box sx={{ height: 20 }} />
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
                <Button style={{ float: "right" }} onClick={handleOnClick} variant="contained" size="">Commit</Button>
            </Container>
        </React.Fragment>
    );
}