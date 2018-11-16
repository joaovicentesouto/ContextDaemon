<?php
namespace SmartData;

require_once('../bin/smartdata/SmartAPI.php');
// use function SmartData\SmartAPI\get;
use SmartData\Exception\CustomException;
use SmartData\Logger;

$response = "";

http_response_code(HttpStatusCode::BAD_REQUEST);

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Content-Type, Content-length, X-Requested-With');
header("Access-Control-Allow-Methods: POST");

try {
    $content = file_get_contents('php://input');
    // $response = get($content);

    $_root  = "/smartdata/*******";// __DIR__;
    $pushd = "pushd {$_root}/workflow/{$this->_domain} > /dev/null";
    $popd  = 'popd > /dev/null';

    $response = shell_exec("{$pushd}; ./wf6 '{$$content}'; {$popd};");
    // $json = json_decode($return, false, 512, JSON_BIGINT_AS_STRING);

}catch(CustomException $e){
    http_response_code($e->getHTTPCodeError());
    Logger::exception($e);
    return false;
}catch(\Exception $e){
    http_response_code(HttpStatusCode::BAD_REQUEST);
    Logger::exception($e);
    return false;
}

http_response_code(HttpStatusCode::OK);
echo $response;

?>
