export const uploadToCloudinary = async (pics) => {

    const cloud_name="dg739lsuu"
    if (pics) {
      
      const data = new FormData();
      data.append("file", pics);
      data.append("upload_preset", "ml_default");
      data.append("cloud_name", cloud_name);
    }
}