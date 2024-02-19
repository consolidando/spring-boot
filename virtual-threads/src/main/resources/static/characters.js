const _CHARACTERS_RESOURCE = "apis/characters";

function characterStartUp(beforeTest, startTest)
{
    var beforeTestElement = document.getElementById(beforeTest);
    if (beforeTestElement)
    {
        beforeTestElement.addEventListener("click", function ()
        {
            charactersGetAll(0);
        });
    }

    var startTestElement = document.getElementById(startTest);
    if (startTestElement)
    {
        startTestElement.addEventListener("click", function ()
        {
            callCharactersGetAllInParallel();
        });
    }
}


function callCharactersGetAllInParallel(parallelCalls = 10)
{
    const startTime = Date.now();
    const promises = [];

    for (let i = 0; i < parallelCalls; i++)
    {
        promises.push(charactersGetAll(i));
    }

    try
    {
        Promise.all(promises)
                .then(() =>
                {
                    const totalTime = Date.now() - startTime;
                    console.log(`--- Total time taken for ${parallelCalls} calls: ${totalTime} ms`);
                    console.log("--- All calls to charactersGetAll() completed successfully.");
                });
    } catch (error) {
        console.error("Error in one or more calls to charactersGetAll():", error);
}
}

function charactersGetAll(grupNumber)
{
    return new Promise((resolve, reject) =>
    {
        const startTime = Date.now();

        charactersGetIds()
                .then(ids =>
                {
                    const requests = ids.map(id => charactersGetIdWithTiming(grupNumber, id.id));

                    return Promise.all(requests);
                })
                .then(characters =>
                {
                    const totalTime = Date.now() - startTime;
                    console.log(`Total time taken by ${grupNumber} : ${totalTime} ms`);
                    resolve(characters);
                })
                .catch(error =>
                {
                    console.error("Error fetching characters:", error);
                    reject(error);
                });
    });
}

function charactersGetIdWithTiming(grupNumber, id)
{
    return new Promise((resolve, reject) =>
    {
        const requestStartTime = Date.now();
        fetch(_CHARACTERS_RESOURCE + "/" + id,
                {
                    method: 'GET',
                    headers:
                            {
                                'Content-Type': 'application/json'
                            }
                })
                .then(response =>
                {
                    const requestEndTime = Date.now();
                    const requestTime = requestEndTime - requestStartTime;
                    console.log(`Grup: ${grupNumber} - Character ${id} fetched in ${requestTime} ms`);
                    return response.json();
                })
                .then(character =>
                {
                    resolve(character);
                })
                .catch(error =>
                {
                    reject(error);
                });
    });
}

function charactersGetIds()
{
    return new Promise((resolve, reject) =>
    {
        fetch(_CHARACTERS_RESOURCE + "/ids",
                {
                    method: 'GET',
                    headers:
                            {
                                'Content-Type': 'application/json'
                            }
                })
                .then(response =>
                {
                    if (!response.ok)
                    {
                        return response.json().then(errorBody =>
                        {
                            throw {
                                status: response.status,
                                statusText: response.statusText,
                                body: errorBody};
                        });
                    }
                    return response.json();
                })
                .then(data => resolve(data))
                .catch(error => reject(error));
    });
}

function charactersGet(id)
{
    return new Promise((resolve, reject) =>
    {
        fetch(_CHARACTERS_RESOURCE + "/" + id,
                {
                    method: 'GET',
                    headers:
                            {
                                'Content-Type': 'application/json'
                            }
                })
                .then(response =>
                {
                    if (!response.ok)
                    {
                        return response.json().then(errorBody =>
                        {
                            throw {
                                status: response.status,
                                statusText: response.statusText,
                                body: errorBody};
                        });
                    }
                    return response.json();
                })
                .then(data => resolve(data))
                .catch(error => reject(error));
    });
}