import { BASE_URL, LOGIN_URL } from "./GeneralConstants";

class BackendClient {
  constructor() {
    this.jwt = "";
  }

  async getFriends() {
    return this.sendRequest(`${BASE_URL}/api/conversation/friends`);
  }

  login = async (loginRequestPayload) => {
    return this.sendRequest(LOGIN_URL, {
      method: "POST",
      body: JSON.stringify(loginRequestPayload),
      headers: {
        "content-type": "application/json",
      },
    });
  };

  getUnseenMessages = async (fromUserId) => {
    let url = `${BASE_URL}/api/conversation/unseenMessages`;
    if (fromUserId) {
      url = url + `/${fromUserId}`;
    }
    return this.sendRequest(url);
  };

  setReadMessages = async (chatMessages) => {
    return this.sendRequest(`${BASE_URL}/api/conversation/setReadMessages`, {
      method: "PUT",
      body: JSON.stringify(chatMessages),
      headers: {
        "content-type": "application/json",
      },
    });
  };

  uploadFile = async (file) => {
    const formData = new FormData();
    formData.append("file", file);
    const response = await fetch(`${BASE_URL}/upload`, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${this.jwt}`
        // Note: Do not set Content-Type header to let browser set it to multipart/form-data
      },
      body: formData,
    });
    return await response.json();
  };

  sendRequest = async (url, apiConfigs) => {
    if (!apiConfigs) {
      apiConfigs = { method: "GET" };
    }
    let { headers } = apiConfigs;
    if (!headers) {
      headers = {};
    }
    headers["Authorization"] = `Bearer ${this.jwt}`;
    apiConfigs.headers = headers;

    const response = await fetch(url, apiConfigs);
    return await response.json();
  };
}

let backendClient = new BackendClient();

export default backendClient;
