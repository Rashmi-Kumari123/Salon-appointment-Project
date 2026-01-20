import { createTheme } from "@mui/material";

const greenTheme = createTheme({
  palette: {
    mode: "light",
    primary: {
      main: "#019031", // a nice green shade
    },
    secondary: {
      main: "#EAF0F1", // light background shade
    },
  },
});

export default greenTheme;
