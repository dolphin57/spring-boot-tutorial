package com.eric.proto;

import io.grpc.stub.StreamObserver;

public class GrpcService extends  BaseServiceGrpc.BaseServiceImplBase {
    @Override
    public void method(BaseGrpcService.Request request, StreamObserver<BaseGrpcService.Response> responseObserver) {
        Data.Response response=null;
        if(request.getType().equals("user")){
            response=getUser(request);
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listMethod(BaseGrpcService.Request request, StreamObserver<BaseGrpcService.ListResponse> responseObserver) {
        Data.ListResponse response=null;
        response=getGirlFriends(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public Data.Response getUser(Data.Request request){
        if("张三".equals(request.getName())){
            Data.User user= Data.User.newBuilder()
                    .setId(1L)
                    .setName("张三")
                    .setAddress("北京市东城区南竹杆胡同2号银河SOHO")
                    .setPassword("123456")
                    .build();
            return Data.Response.newBuilder()
                    .setCode("200")
                    .setMessage("成功")
                    .setData(user.toByteString())
                    .build();
        }else{
            return Data.Response.newBuilder()
                    .setCode("404")
                    .setMessage("无此用户")
                    .build();
        }
    }

    public Data.ListResponse getGirlFriends(Data.Request request){
        if("张三".equals(request.getName())){
            List<ByteString> list=new ArrayList<>();
            Data.GirFriend girFriend= Data.GirFriend.newBuilder()
                    .setName("迪丽热巴")
                    .setAge("25")
                    .setAddress("新疆维吾尔自治区乌鲁木齐市")
                    .build();
            list.add(girFriend.toByteString());

            girFriend= Data.GirFriend.newBuilder()
                    .setName("杨幂")
                    .setAge("30")
                    .setAddress("北京市宣武区")
                    .build();
            list.add(girFriend.toByteString());

            girFriend= Data.GirFriend.newBuilder()
                    .setName("项思醒")
                    .setAge("24")
                    .setAddress("杭州")
                    .build();

            list.add(girFriend.toByteString());

            Data.ListResponse.Builder builder = Data.ListResponse.newBuilder()
                    .setCode("200")
                    .setMessage("成功");
            for (int i = 0; i < list.size(); i++) {
                builder.addData(list.get(i));
            }
            return builder.build();
        }else{
            return Data.ListResponse.newBuilder()
                    .setCode("404")
                    .setMessage("无此用户")
                    .build();
        }
    }
}
